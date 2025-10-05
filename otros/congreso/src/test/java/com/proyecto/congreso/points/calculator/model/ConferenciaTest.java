package com.proyecto.congreso.points.calculator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConferenciaTest {
    private static final String ID = "C005";
    private static final String TITULO_CONFERENCIA = "Neurotecnología y ética";
    private static final Integer PUNTOS_GANADOS = 5;
    private static final String DIA = "Martes";
    private static final String PONENTE_NOMBRE = "Dra. Elara Vazquez";

    // -------------------------------------------------------------------------
    // Test: Constructor con todos los argumentos (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateEntityWithAllFieldsSet() {
        // Given
        Conferencia conferencia = new Conferencia(
                ID,
                DIA,
                "2025-10-06",
                "10:00",
                "12:00",
                "2.0",
                "Auditorio Principal",
                "Seminario",
                TITULO_CONFERENCIA,
                PONENTE_NOMBRE,
                PUNTOS_GANADOS
        );

        // Then
        assertNotNull(conferencia, "La entidad no debe ser nula.");
        assertEquals(ID, conferencia.getConferenciaId(), "El ID no coincide.");
        assertEquals(TITULO_CONFERENCIA, conferencia.getTitulo(), "El título no coincide.");
        assertEquals(PUNTOS_GANADOS, conferencia.getPuntos(), "Los puntos no coinciden.");
        assertEquals(DIA, conferencia.getDia(), "El día no coincide.");
        assertEquals(PONENTE_NOMBRE, conferencia.getPonente(), "El ponente no coincide.");
    }

    // -------------------------------------------------------------------------
    // Test: Constructor sin argumentos (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        Conferencia conferencia = new Conferencia(); // Usa el constructor sin argumentos
        Integer nuevosPuntos = 8;

        // When
        conferencia.setConferenciaId(ID);
        conferencia.setTitulo(TITULO_CONFERENCIA);
        conferencia.setPuntos(nuevosPuntos);
        conferencia.setDia(DIA);

        // Then
        assertEquals(ID, conferencia.getConferenciaId(), "Getter/Setter de ID falló.");
        assertEquals(TITULO_CONFERENCIA, conferencia.getTitulo(), "Getter/Setter de Título falló.");
        assertEquals(nuevosPuntos, conferencia.getPuntos(), "Getter/Setter de Puntos falló.");
        assertEquals(DIA, conferencia.getDia(), "Getter/Setter de Día falló.");
    }

    // -------------------------------------------------------------------------
    // Test: Mapeo de valores nulos
    // -------------------------------------------------------------------------

    @Test
    void shouldHandleNullValuesGracefully() {
        // Given
        Conferencia conferencia = new Conferencia();

        // When
        conferencia.setConferenciaId(ID);
        conferencia.setPonente(null);
        conferencia.setPuntos(null);

        // Then
        assertEquals(ID, conferencia.getConferenciaId(), "El ID debe persistir.");
        assertNull(conferencia.getPonente(), "El ponente debe ser nulo.");
        assertNull(conferencia.getPuntos(), "Los puntos deben ser nulos.");
    }
}
