package com.proyecto.congreso.service;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import com.proyecto.congreso.pases.events.CertificateEvent;
import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.CertificateRepository;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.pases.service.PassPointsEventHandler;
import com.proyecto.congreso.points.assistance.events.AssistanceRegisteredEvent;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.events.FreebieStockReservedEvent;
import com.proyecto.congreso.points.exchange.events.ExchangeFailedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassPointsEventHandlerTest {

    private static final Long PASS_ID = 1L;
    private static final Long PARTICIPANT_ID = 10L;
    private static final String FREEBIE_ID = "F001";
    private static final Integer POINTS_CERTIFICATE = 25; // Asumido de la entidad Pass
    private static final Integer POINTS_SPECIAL_ACCESS = 30; // Asumido de la entidad Pass

    @Mock private PassRepository passRepository;
    @Mock private ParticipantRepository participantRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    // FreebieRepository no se usa en la lógica, pero se mockea por RequiredArgsConstructor
    @Mock private FreebieRepository freebieRepository;

    @InjectMocks
    private PassPointsEventHandler eventHandler;

    private Pass activePass;
    private Participant testParticipant;

    @BeforeEach
    void setUp() {
        activePass = new Pass();
        activePass.setPassId(PASS_ID);
        activePass.setParticipantId(PARTICIPANT_ID);
        activePass.setStatus(Pass.PassStatus.ACTIVE);
        activePass.setPointsBalance(10); // Balance inicial para los tests
        activePass.setPointsCertificate(POINTS_CERTIFICATE);
        activePass.setPointsSpecialAccess(POINTS_SPECIAL_ACCESS);

        testParticipant = new Participant();
        testParticipant.setParticipantId(PARTICIPANT_ID);
        testParticipant.setName("Jane");
        testParticipant.setLastName("Doe");
        testParticipant.setEmail("jane.doe@example.com");

        // Mockear el factory method de Certificate para evitar llamadas a su lógica interna
        try (MockedStatic<Certificate> mockedCertificate = mockStatic(Certificate.class)) {
            mockedCertificate.when(() -> Certificate.create(any(), any(), any(), any(), any()))
                    .thenAnswer(invocation -> new Certificate());
        } catch (Exception e) {
            // Ignorar excepción si Mockito no soporta static mocking fácilmente
        }
    }

    // -------------------------------------------------------------------------
    // Test: handleAssistanceRegistered (Sumar Puntos)
    // -------------------------------------------------------------------------

    @Test
    void handleAssistanceRegistered_shouldSumPointsSuccessfully() {

        ArgumentCaptor<Pass> passCaptor = ArgumentCaptor.forClass(Pass.class);
        AssistanceRegisteredEvent event = mock(AssistanceRegisteredEvent.class);
        Integer pointsToAdd = 5;

        // Mantenemos la Pass activa como la que se encuentra y la que se guarda
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));

        // OPCIONAL: Asegurar que el save devuelve la misma instancia modificada (aunque no estrictamente
        // necesario aquí, previene problemas de caché o inmutabilidad implícita).
        when(passRepository.save(any(Pass.class))).thenReturn(activePass); // <--- Opcional pero recomendado

        // When
        when(event.getPassId()).thenReturn(PASS_ID); // <--- ¡Esta línea es crucial!
        when(event.getAmountPoints()).thenReturn(pointsToAdd);
        eventHandler.handleAssistanceRegistered(event);

        // 2. Capturamos la instancia de Pass que fue pasada al método save()
        verify(passRepository).save(passCaptor.capture());

        // 3. Obtenemos el objeto Pass capturado
        Pass savedPass = passCaptor.getValue();

        // 4. Realizamos las aserciones sobre el objeto capturado (savedPass)
        assertEquals(15, savedPass.getPointsBalance(), "El balance debe ser 10 + 5 = 15.");
        assertEquals(Pass.PointsMovementAdd.ADD, savedPass.getPointsAdd(), "Debe establecer el movimiento.");

        verify(passRepository).findById(PASS_ID);
        // Verificamos que no se publiquen eventos de logros (ya que 15 < 25)
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handleAssistanceRegistered_shouldNotSumPointsIfPassIsNotActive() {
        // Given
        activePass.setStatus(Pass.PassStatus.CLOSED);
        AssistanceRegisteredEvent event = new AssistanceRegisteredEvent(PASS_ID, 5);

        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));

        // When
        eventHandler.handleAssistanceRegistered(event);

        // Then
        assertEquals(10, activePass.getPointsBalance(), "El balance no debe cambiar.");
        verify(passRepository, never()).save(any(Pass.class));
    }

    // -------------------------------------------------------------------------
    // Test: Logros Desbloqueados
    // -------------------------------------------------------------------------

    @Test
    void handleAssistanceRegistered_shouldReachCertificateAchievement() {
        ArgumentCaptor<Pass> passCaptor = ArgumentCaptor.forClass(Pass.class);

        // Given
        Integer pointsToAdd = 15;
        activePass.setPointsBalance(10);

        // 💡 SOLUCIÓN: Usar un mock del evento para garantizar que los getters funcionen
        AssistanceRegisteredEvent event = mock(AssistanceRegisteredEvent.class);
        when(event.getPassId()).thenReturn(PASS_ID); // ¡CLAVE! Asegura que el ID no es null
        when(event.getAmountPoints()).thenReturn(pointsToAdd);

        // Configuración del Mock
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));
        when(passRepository.save(any(Pass.class))).thenReturn(activePass);

        // When
        eventHandler.handleAssistanceRegistered(event);

        // Then
        // La línea verify(passRepository).save(passCaptor.capture()); ahora será invocada
        verify(passRepository).save(passCaptor.capture());
        Pass savedPass = passCaptor.getValue();

        // Aserciones sobre el objeto capturado
        assertEquals(POINTS_CERTIFICATE, savedPass.getPointsBalance());
        assertEquals(Pass.CertificateStatus.REACHED, savedPass.getCertificateStatus());

        verify(passRepository).findById(PASS_ID);
        verify(eventPublisher).publishEvent(any(CertificateEvent.class));
        verify(eventPublisher, never()).publishEvent(any(ExchangeFailedEvent.class));
    }

    @Test
    void handleAssistanceRegistered_shouldNotPublishCertificateEventIfAlreadyReached() {
        // 1. Definir el ArgumentCaptor para la clase Pass
        ArgumentCaptor<Pass> passCaptor = ArgumentCaptor.forClass(Pass.class);

        // Given
        // Ya alcanzó el certificado (esto debería evitar que se publique el CertificateEvent)
        activePass.setCertificateStatus(Pass.CertificateStatus.REACHED);
        activePass.setPointsBalance(POINTS_CERTIFICATE); // 25 puntos

        Integer pointsToAdd = 5;

        // 💡 SOLUCIÓN: Usar un mock del evento para garantizar que los getters funcionen
        AssistanceRegisteredEvent event = mock(AssistanceRegisteredEvent.class);
        when(event.getPassId()).thenReturn(PASS_ID);
        when(event.getAmountPoints()).thenReturn(pointsToAdd); // 5 puntos

        // Configuración del Mock
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));
        // Es buena práctica que el save devuelva el objeto modificado
        when(passRepository.save(any(Pass.class))).thenReturn(activePass);

        // When
        eventHandler.handleAssistanceRegistered(event);

        // Then
        // 2. Capturamos la instancia de Pass que fue pasada al método save()
        verify(passRepository).save(passCaptor.capture());
        Pass savedPass = passCaptor.getValue(); // El objeto final guardado

        // 3. Aserciones sobre el objeto capturado
        // El valor esperado es 25 (inicial) + 5 (suma) = 30
        assertEquals(POINTS_CERTIFICATE + 5, savedPass.getPointsBalance(),
                "El balance final debe ser 30 (25 + 5).");

        // Verificamos que se haya alcanzado el Acceso Especial (30 puntos)
        assertEquals(Pass.AccessStatus.REACHED, savedPass.getAccessStatus(),
                "El AccessStatus debe ser REACHED, ya que se alcanzaron 30 puntos.");

        // Verificamos la regla principal del test: no publicar evento duplicado
        verify(eventPublisher, never()).publishEvent(any(CertificateEvent.class));
    }

    @Test
    void shouldReachBothCertificateAndSpecialAccessAchievements() {
        // 1. Definir el ArgumentCaptor
        ArgumentCaptor<Pass> passCaptor = ArgumentCaptor.forClass(Pass.class);

        // Given
        // Balance de 10. Sumamos 20. Nuevo Balance: 30. (Alcanza 25 y 30)
        Integer pointsToAdd = 20;
        activePass.setPointsBalance(10);

        // Configuración de Mocks del evento
        AssistanceRegisteredEvent event = mock(AssistanceRegisteredEvent.class);
        when(event.getPassId()).thenReturn(PASS_ID);
        when(event.getAmountPoints()).thenReturn(pointsToAdd);

        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));
        when(passRepository.save(any(Pass.class))).thenReturn(activePass);

        // When
        eventHandler.handleAssistanceRegistered(event);

        // Then
        verify(passRepository).save(passCaptor.capture());
        Pass savedPass = passCaptor.getValue();

        // ✅ Asersiones de Estado
        assertEquals(POINTS_SPECIAL_ACCESS, savedPass.getPointsBalance(), "El balance final debe ser 30.");
        assertEquals(Pass.AccessStatus.REACHED, savedPass.getAccessStatus(), "AccessStatus debe ser REACHED.");
        assertEquals(Pass.CertificateStatus.REACHED, savedPass.getCertificateStatus(), "CertificateStatus también debe ser REACHED."); // NUEVA VERIFICACIÓN

        // ✅ Asersiones de Eventos
        verify(eventPublisher).publishEvent(any(CertificateEvent.class)); // AHORA QUEREMOS QUE SE INVOQUE
        // verify(eventPublisher).publishEvent(any(SpecialAccessEvent.class)); // Si tuvieras un evento para esto
    }

    // -------------------------------------------------------------------------
    // Test: handleStockReserved (Usar Puntos)
    // -------------------------------------------------------------------------

    @Test
    void handleStockReserved_shouldDiscountPointsSuccessfully() {
        // Given
        Integer pointsToUse = 5;
        activePass.setPointsBalance(10);
        FreebieStockReservedEvent event = new FreebieStockReservedEvent(PASS_ID, "4", 5);

        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));

        // When
        eventHandler.handleStockReserved(event);

        // Then
        assertEquals(5, activePass.getPointsBalance(), "El balance debe ser 10 - 5 = 5.");
        verify(passRepository).save(activePass);
        verify(eventPublisher, never()).publishEvent(any(ExchangeFailedEvent.class));
    }

    @Test
    void handleStockReserved_shouldPublishFailureEventIfPointsInsufficient() {
        // 1. Definir los valores esperados
        Integer pointsToUse = 15; // Costo alto
        Long NON_EXISTENT_PASS_ID = 99L; // Usar un ID que no exista para la prueba de fallo

        // Given
        activePass.setPointsBalance(10); // Balance bajo (10 < 15)

        // 2. Usar un Mock para garantizar el comportamiento (incluyendo el costo)
        FreebieStockReservedEvent event = mock(FreebieStockReservedEvent.class);
        when(event.getPassId()).thenReturn(PASS_ID);
        when(event.getCosto()).thenReturn(pointsToUse); // ⬅️ ¡CLAVE! El costo es 15
        when(event.getFreebieId()).thenReturn("F001");

        // Mock para que encuentre el Pass
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));

        // When
        eventHandler.handleStockReserved(event);

        // Then
        // 3. Aserciones
        assertEquals(10, activePass.getPointsBalance(), "El balance no debe cambiar si los puntos son insuficientes.");

        // El servicio NO debe llamar a save()
        verify(passRepository, never()).save(any(Pass.class));

        // El servicio SÍ debe publicar el evento de fallo
        ArgumentCaptor<ExchangeFailedEvent> captor = ArgumentCaptor.forClass(ExchangeFailedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals(PASS_ID, captor.getValue().getPassId());
    }

    @Test
    void handleStockReserved_shouldPublishFailureEventIfPassNotFound() {
        // Given
        FreebieStockReservedEvent event = new FreebieStockReservedEvent(PASS_ID, "10", 5);
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.empty());

        // When
        eventHandler.handleStockReserved(event);

        // Then
        verify(passRepository, never()).save(any(Pass.class));

        // Verifica que se publique el evento de fallo
        ArgumentCaptor<ExchangeFailedEvent> captor = ArgumentCaptor.forClass(ExchangeFailedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals(PASS_ID, captor.getValue().getPassId());
    }

    // -------------------------------------------------------------------------
    // Test: handleCertificateAchieved (Crear Certificado)
    // -------------------------------------------------------------------------

    @Test
    void handleCertificateAchieved_shouldCreateCertificateSuccessfully() {
        // Given
        CertificateEvent event = new CertificateEvent(PASS_ID);
        Certificate mockSavedCertificate = new Certificate();
        mockSavedCertificate.setCertificateId(99L); // ID simulado después de guardar

        when(certificateRepository.existsByPassId(PASS_ID)).thenReturn(false);
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(activePass));
        when(participantRepository.findById(PARTICIPANT_ID)).thenReturn(Optional.of(testParticipant));
        when(certificateRepository.save(any(Certificate.class))).thenReturn(mockSavedCertificate);

        // When
        eventHandler.handleCertificateAchieved(event);

        // Then
        verify(certificateRepository).existsByPassId(PASS_ID);
        verify(passRepository).findById(PASS_ID);
        verify(participantRepository).findById(PARTICIPANT_ID);

        // Verifica que se llamó al guardado del certificado
        verify(certificateRepository).save(any(Certificate.class));
        // Nota: Se asume que Certificate.create() es mockeado o no falla
    }

    @Test
    void handleCertificateAchieved_shouldReturnIfCertificateAlreadyExists() {
        // Given
        CertificateEvent event = new CertificateEvent(PASS_ID);
        when(certificateRepository.existsByPassId(PASS_ID)).thenReturn(true);

        // When
        eventHandler.handleCertificateAchieved(event);

        // Then
        // Ningún otro repositorio o evento debe ser llamado
        verify(certificateRepository).existsByPassId(PASS_ID);
        verify(passRepository, never()).findById(anyLong());
        verify(certificateRepository, never()).save(any(Certificate.class));
    }

    @Test
    void handleCertificateAchieved_shouldHandlePassNotFoundException() {
        // Given
        CertificateEvent event = new CertificateEvent(PASS_ID);
        when(certificateRepository.existsByPassId(PASS_ID)).thenReturn(false);
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.empty());

        // When
        // La excepción es atrapada dentro del handler, por lo que no se lanza al test.
        eventHandler.handleCertificateAchieved(event);

        // Then
        verify(passRepository).findById(PASS_ID);
        verify(participantRepository, never()).findById(anyLong());
        verify(certificateRepository, never()).save(any(Certificate.class));
        // La excepción se maneja, el test pasa si no se lanza nada.
    }
}
