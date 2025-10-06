package com.proyecto.congreso.points.assistance.service;

import com.proyecto.congreso.points.assistance.model.Asistencia;
import com.proyecto.congreso.points.calculator.model.Conferencia;
import com.proyecto.congreso.points.assistance.repository.AsistenciaRepository;
import com.proyecto.congreso.points.calculator.repository.ConferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssistanceService - Tests Unitarios")
class AssistanceServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AssistanceService assistanceService;

    private Conferencia conferenciaTest;
    private Asistencia asistenciaTest;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        conferenciaTest = new Conferencia();
        conferenciaTest.setConferenciaId("1L");
        conferenciaTest.setTitulo("Introducción a IA");
        conferenciaTest.setPuntos(5);
        conferenciaTest.setTipo("Conferencia");
        conferenciaTest.setSede("Auditorio Principal");

        asistenciaTest = new Asistencia();
        asistenciaTest.setId("mongo-id-123");
        asistenciaTest.setPassId(100L);
        asistenciaTest.setConferenciaId("1L");
        asistenciaTest.setTituloConferencia("Introducción a IA");
        asistenciaTest.setPuntosOtorgados(5);
        asistenciaTest.setStatus("PROCESADA");
    }

//    @Test
//    @DisplayName("✅ Debe marcar asistencia exitosamente y publicar evento")
//    void debeMarcarAsistenciaExitosamente() {
//        // Given
//        Long passId = 100L;
//        String conferenciaId = "1L";
//
//        when(conferenceRepository.findById(conferenciaId))
//                .thenReturn(Optional.of(conferenciaTest));
//        when(asistenciaRepository.existsByPassIdAndConferenciaId(passId, conferenciaId))
//                .thenReturn(false);
//        when(asistenciaRepository.save(any(Asistencia.class)))
//                .thenReturn(asistenciaTest);
//
//        // When
//        AssistanceResponse response = assistanceService.marcarAsistencia(passId, conferenciaId);
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.getPassId()).isEqualTo(passId);
//        assertThat(response.getConferenciaId()).isEqualTo(conferenciaId);
//        assertThat(response.getPuntosOtorgados()).isEqualTo(5);
//
//        // Verificar que se guardó en MongoDB
//        verify(asistenciaRepository, times(1)).save(any(Asistencia.class));
//
//        // Verificar que se publicó el evento con los datos correctos
//        ArgumentCaptor<AssistanceRegisteredEvent> eventCaptor =
//                ArgumentCaptor.forClass(AssistanceRegisteredEvent.class);
//        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
//
//        AssistanceRegisteredEvent event = eventCaptor.getValue();
//        assertThat(event.getPassId()).isEqualTo(passId);
//        assertThat(event.getConferenciaId()).isEqualTo(conferenciaId);
//        assertThat(event.getAmountPoints()).isEqualTo(5);
//        assertThat(event.getTituloConferencia()).isEqualTo("Introducción a IA");
//    }

    @Test
    @DisplayName("❌ Debe fallar si la conferencia no existe")
    void debeFallarSiConferenciaNoExiste() {
        // Given
        Long passId = 100L;
        String conferenciaId = "999L";

        when(conferenceRepository.findById(conferenciaId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> assistanceService.marcarAsistencia(passId, conferenciaId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Conferencia no encontrada: 999");

        // No debe guardar ni publicar eventos
        verify(asistenciaRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

//    @Test
//    @DisplayName("❌ Debe fallar si ya existe asistencia duplicada")
//    void debeFallarSiAsistenciaDuplicada() {
//        // Given
//        Long passId = 100L;
//        String conferenciaId = "1L";
//
//        when(conferenceRepository.findById(conferenciaId))
//                .thenReturn(Optional.of(conferenciaTest));
//        when(asistenciaRepository.existsByPassIdAndConferenciaId(passId, conferenciaId))
//                .thenReturn(true); // Ya existe
//
//        // When & Then
//        assertThatThrownBy(() -> assistanceService.marcarAsistencia(passId, conferenciaId))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Ya existe registro de asistencia");
//
//        // No debe guardar ni publicar eventos
//        verify(asistenciaRepository, never()).save(any());
//        verify(eventPublisher, never()).publishEvent(any());
//    }

    @Test
    @DisplayName("✅ Debe calcular total de puntos acumulados correctamente")
    void debeCalcularTotalPuntosAcumulados() {
        // Given
        Long passId = 100L;
        
        Asistencia a1 = new Asistencia();
        a1.setPuntosOtorgados(5);
        
        Asistencia a2 = new Asistencia();
        a2.setPuntosOtorgados(3);
        
        Asistencia a3 = new Asistencia();
        a3.setPuntosOtorgados(2);

        when(asistenciaRepository.findAsistenciasProcedasByPass(passId))
                .thenReturn(java.util.List.of(a1, a2, a3));

        // When
        Integer totalPuntos = assistanceService.getTotalPuntosAcumulados(passId);

        // Then
        assertThat(totalPuntos).isEqualTo(10);
    }

    @Test
    @DisplayName("✅ Debe contar asistencias correctamente")
    void debeContarAsistenciasCorrectamente() {
        // Given
        Long passId = 100L;
        when(asistenciaRepository.countByPassId(passId)).thenReturn(5L);

        // When
        long count = assistanceService.countAsistenciasByPass(passId);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(asistenciaRepository, times(1)).countByPassId(passId);
    }
}
