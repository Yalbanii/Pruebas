package com.proyecto.congreso.points.assistance.model;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class AsistenciaTest {

    private static final String ID = "MONGO-ID-123";
    private static final Long PASS_ID = 456L;
    private static final Long PARTICIPANT_ID = 789L;
    private static final String CONFERENCIA_ID = "CONF-XYZ";
    private static final String TITULO = "Desarrollo Ágil";
    private static final Integer PUNTOS = 10;
    private static final LocalDateTime FECHA = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final String STATUS = "FINALIZADO";

    // -------------------------------------------------------------------------
    // Test 1: Constructor Completo (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // When
        Asistencia asistencia = new Asistencia(
                ID, PASS_ID, PARTICIPANT_ID, CONFERENCIA_ID, TITULO, PUNTOS, FECHA, STATUS
        );

        // Then
        assertNotNull(asistencia);
        assertEquals(ID, asistencia.getId());
        assertEquals(PASS_ID, asistencia.getPassId());
        assertEquals(PARTICIPANT_ID, asistencia.getParticipantId());
        assertEquals(PUNTOS, asistencia.getPuntosOtorgados());
        assertEquals(FECHA, asistencia.getFechaAsistencia());
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Vacío (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        Asistencia asistencia = new Asistencia();

        // When
        asistencia.setConferenciaId(CONFERENCIA_ID);
        asistencia.setTituloConferencia(TITULO);
        asistencia.setPuntosOtorgados(PUNTOS);

        // Then
        assertEquals(CONFERENCIA_ID, asistencia.getConferenciaId(), "Getter/Setter de Conferencia ID falló.");
        assertEquals(TITULO, asistencia.getTituloConferencia(), "Getter/Setter de Título falló.");
        assertEquals(PUNTOS, asistencia.getPuntosOtorgados(), "Getter/Setter de Puntos falló.");
        assertNull(asistencia.getId(), "El ID debe ser nulo si no se setea.");
    }

    // -------------------------------------------------------------------------
    // Test 3: Método de Fábrica Estático (crear)
    // -------------------------------------------------------------------------

    @Test
    void crear_shouldInitializeWithInputParametersAndDefaultValues() {
        // Given: Mockear LocalDateTime.now() para obtener un tiempo fijo
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 5, 10, 30, 15);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            // When
            Asistencia asistencia = Asistencia.crear(
                    PASS_ID, PARTICIPANT_ID, CONFERENCIA_ID, TITULO, PUNTOS
            );

            // Then
            assertNotNull(asistencia);

            // 1. Verificar parámetros de entrada
            assertEquals(PASS_ID, asistencia.getPassId());
            assertEquals(PARTICIPANT_ID, asistencia.getParticipantId());
            assertEquals(TITULO, asistencia.getTituloConferencia());
            assertEquals(PUNTOS, asistencia.getPuntosOtorgados());

            // 2. Verificar valores por defecto/calculados
            assertNull(asistencia.getId(), "El ID no debe asignarse en el método crear.");
            assertEquals("PROCESADA", asistencia.getStatus(), "El status por defecto debe ser 'PROCESADA'.");
            assertEquals(fixedTime, asistencia.getFechaAsistencia(), "La fecha de asistencia debe ser LocalDateTime.now() mockeado.");
        }
    }
}
