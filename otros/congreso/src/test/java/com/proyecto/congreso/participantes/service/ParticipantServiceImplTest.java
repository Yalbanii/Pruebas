package com.proyecto.congreso.participantes.service;
import com.proyecto.congreso.participantes.model.Participant;
import com.proyecto.congreso.participantes.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Permite usar las anotaciones @Mock y @InjectMocks
@ExtendWith(MockitoExtension.class)
public class ParticipantServiceImplTest {

    // Simula la dependencia del repositorio
    @Mock
    private ParticipantRepository participantRepository;

    // Simula la dependencia del publicador de eventos
    @Mock
    private ApplicationEventPublisher eventPublisher;

    // Inyecta los mocks en la instancia real del servicio que vamos a probar
    @InjectMocks
    private ParticipantServiceImpl participantService;

    // --- Datos de Prueba Consistentes ---
    private Participant participanteActivo;
    private final Long ID_PRUEBA = 1L;
    private final String EMAIL_PRUEBA = "test@ejemplo.com";
    private final String EMAIL_EXISTENTE = "existente@ejemplo.com";

    @BeforeEach
    void setUp() {
        // Inicializa un participante base para usar en todos los tests
        participanteActivo = new Participant();
        participanteActivo.setParticipantId(ID_PRUEBA);
        participanteActivo.setName("MockName");
        participanteActivo.setEmail(EMAIL_PRUEBA);
        participanteActivo.setStatus(Participant.ParticipantStatus.ACTIVE);
    }

    // =========================================================================
    // 1. Cobertura del Método createParticipant
    // =========================================================================

    @Test
    void createParticipant_DebeLanzarExcepcion_CuandoElEmailYaExiste() {
        // Arrange
        Participant participanteConEmailExistente = new Participant();
        participanteConEmailExistente.setEmail(EMAIL_EXISTENTE);

        // Mock: existsByEmail debe devolver true
        when(participantRepository.existsByEmail(EMAIL_EXISTENTE)).thenReturn(true);

        // Act & Assert (Cubre el camino de excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            participantService.createParticipant(participanteConEmailExistente);
        }, "Debe lanzar IllegalArgumentException si el email existe.");

        // Verificación: save y publishEvent NO deben ser llamados
        verify(participantRepository, never()).save(any(Participant.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    // =========================================================================
    // 2. Cobertura de getParticipantById y Casos de No Encontrado
    // =========================================================================

    @Test
    void getParticipantById_DebeDevolverParticipante_CuandoExiste() {
        // Mock: findById debe devolver un Optional con el participante
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));

        // Act
        Participant resultado = participantService.getParticipantById(ID_PRUEBA);

        // Assert
        assertNotNull(resultado);
        assertEquals(ID_PRUEBA, resultado.getParticipantId());
    }

    @Test
    void getParticipantById_DebeLanzarExcepcion_CuandoNoExiste() {
        // Mock: findById debe devolver un Optional vacío
        when(participantRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert (Cubre el camino de excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            participantService.getParticipantById(99L);
        }, "Debe lanzar IllegalArgumentException si no encuentra el participante.");
    }

    // =========================================================================
    // 3. Cobertura de updateParticipant
    // =========================================================================

    @Test
    void updateParticipant_DebeActualizarYGuardar_CuandoElEmailNoCambia() {
        // Arrange
        Participant datosActualizados = new Participant();
        datosActualizados.setEmail(EMAIL_PRUEBA); // El email NO cambia
        datosActualizados.setName("NuevoNombre");

        // Mock: getParticipantById es llamado internamente para encontrar el existente
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));
        // Mock: save es llamado para guardar los cambios
        when(participantRepository.save(any(Participant.class))).thenReturn(participanteActivo);

        // Act
        Participant resultado = participantService.updateParticipant(ID_PRUEBA, datosActualizados);

        // Assert
        assertEquals("NuevoNombre", resultado.getName()); // Verifica que el campo se actualizó
        // Verifica que existsByEmail NO fue llamado, ya que el email es el mismo
        verify(participantRepository, never()).existsByEmail(anyString());
        verify(participantRepository, times(1)).save(participanteActivo);
    }

    @Test
    void updateParticipant_DebeActualizarYGuardar_CuandoElEmailCambiaYNoExiste() {
        // Arrange
        Participant datosActualizados = new Participant();
        datosActualizados.setEmail("nuevo.email@ejemplo.com"); // El email CAMBIA
        datosActualizados.setName("NuevoNombre");

        // Mock:
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));
        // Mock: existsByEmail debe devolver false para el nuevo email
        when(participantRepository.existsByEmail("nuevo.email@ejemplo.com")).thenReturn(false);
        when(participantRepository.save(any(Participant.class))).thenReturn(participanteActivo);

        // Act
        Participant resultado = participantService.updateParticipant(ID_PRUEBA, datosActualizados);

        // Assert
        assertEquals("nuevo.email@ejemplo.com", resultado.getEmail());
        // Verifica que existsByEmail FUE llamado
        verify(participantRepository, times(1)).existsByEmail("nuevo.email@ejemplo.com");
    }

    @Test
    void updateParticipant_DebeLanzarExcepcion_CuandoElEmailCambiaYYaExiste() {
        // Arrange
        Participant datosActualizados = new Participant();
        datosActualizados.setEmail(EMAIL_EXISTENTE); // Email diferente al existente

        // Mock:
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));
        // Mock: existsByEmail debe devolver true para el nuevo email
        when(participantRepository.existsByEmail(EMAIL_EXISTENTE)).thenReturn(true);

        // Act & Assert (Cubre el camino de excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            participantService.updateParticipant(ID_PRUEBA, datosActualizados);
        }, "Debe lanzar IllegalArgumentException si el nuevo email ya existe.");

        verify(participantRepository, never()).save(any(Participant.class));
    }

    // =========================================================================
    // 4. Cobertura de Consultas (getAll, getByStatus, existsByEmail)
    // =========================================================================

    @Test
    void getAllParticipants_DebeDevolverLista() {
        // Mock:
        when(participantRepository.findAll()).thenReturn(Collections.singletonList(participanteActivo));

        // Act
        List<Participant> resultado = participantService.getAllParticipants();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void getParticipantByStatus_DebeDevolverListaFiltrada() {
        // Mock:
        when(participantRepository.findByStatus(Participant.ParticipantStatus.ACTIVE))
                .thenReturn(Collections.singletonList(participanteActivo));

        // Act
        List<Participant> resultado = participantService.getParticipantByStatus(Participant.ParticipantStatus.ACTIVE);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(Participant.ParticipantStatus.ACTIVE, resultado.get(0).getStatus());
        verify(participantRepository, times(1)).findByStatus(Participant.ParticipantStatus.ACTIVE);
    }

    @Test
    void existsByEmail_DebeDevolverResultadoDelRepositorio() {
        // Mock:
        when(participantRepository.existsByEmail(EMAIL_PRUEBA)).thenReturn(true);

        // Act & Assert
        assertTrue(participantService.existsByEmail(EMAIL_PRUEBA));
        verify(participantRepository, times(1)).existsByEmail(EMAIL_PRUEBA);
    }

    // =========================================================================
    // 5. Cobertura de Lógica de Estado (delete, activate, deactivate)
    // =========================================================================

    @Test
    void deleteParticipant_DebeCambiarEstadoAInactiveYSeguirElSoftDelete() {
        // Mock: getParticipantById es llamado internamente
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));
        // Mock: save devuelve la entidad
        when(participantRepository.save(any(Participant.class))).thenReturn(participanteActivo);

        // Act
        participantService.deleteParticipant(ID_PRUEBA);

        // Assert
        // Verifica que el estado fue cambiado ANTES de guardarse
        assertEquals(Participant.ParticipantStatus.INACTIVE, participanteActivo.getStatus());
        verify(participantRepository, times(1)).save(participanteActivo);
    }

    @Test
    void activateParticipant_DebeCambiarEstadoAActiveYGuardar() {
        // Arrange
        participanteActivo.setStatus(Participant.ParticipantStatus.INACTIVE); // Lo inicializa como INACTIVE

        // Mock: getParticipantById es llamado internamente
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));
        when(participantRepository.save(any(Participant.class))).thenReturn(participanteActivo);

        // Act
        Participant resultado = participantService.activateParticipant(ID_PRUEBA);

        // Assert
        assertEquals(Participant.ParticipantStatus.ACTIVE, resultado.getStatus());
        verify(participantRepository, times(1)).save(participanteActivo);
    }

    @Test
    void deactivateParticipant_DebeCambiarEstadoAInactiveYGuardar() {
        // Mock: getParticipantById es llamado internamente
        when(participantRepository.findById(ID_PRUEBA)).thenReturn(Optional.of(participanteActivo));
        when(participantRepository.save(any(Participant.class))).thenReturn(participanteActivo);

        // Act
        Participant resultado = participantService.deactivateParticipant(ID_PRUEBA);

        // Assert
        assertEquals(Participant.ParticipantStatus.INACTIVE, resultado.getStatus());
        verify(participantRepository, times(1)).save(participanteActivo);
    }
}
