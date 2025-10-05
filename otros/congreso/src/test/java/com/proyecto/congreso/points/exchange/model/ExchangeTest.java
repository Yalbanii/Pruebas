package com.proyecto.congreso.points.exchange.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class ExchangeTest {
    private static final String ID = "EX-007";
    private static final Long PASS_ID = 101L;
    private static final Long PARTICIPANT_ID = 900L;
    private static final String FREEBIE_ID = "F007";
    private static final String ARTICULO = "Taza de Viaje";
    private static final Integer PUNTOS_REDUCIDOS = 10;
    private static final LocalDateTime FECHA = LocalDateTime.of(2025, 10, 5, 12, 0);

    // -------------------------------------------------------------------------
    // Test 1: Constructor Completo (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateEntityWithAllFieldsSet() {
        // Given
        Exchange exchange = new Exchange(
                ID, PASS_ID, PARTICIPANT_ID, FREEBIE_ID, ARTICULO, PUNTOS_REDUCIDOS, FECHA
        );

        // Then
        assertNotNull(exchange, "La entidad no debe ser nula.");
        assertEquals(ID, exchange.getId(), "El ID no coincide.");
        assertEquals(PASS_ID, exchange.getPassId(), "El Pass ID no coincide.");
        assertEquals(PUNTOS_REDUCIDOS, exchange.getPuntosReducidos(), "Los puntos reducidos no coinciden.");
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Vacío (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        Exchange exchange = new Exchange();

        // When
        exchange.setFreebieId(FREEBIE_ID);
        exchange.setPuntosReducidos(PUNTOS_REDUCIDOS);
        exchange.setArticulo(ARTICULO);

        // Then
        assertEquals(FREEBIE_ID, exchange.getFreebieId(), "Getter/Setter de Freebie ID falló.");
        assertEquals(PUNTOS_REDUCIDOS, exchange.getPuntosReducidos(), "Getter/Setter de Puntos falló.");
        assertEquals(ARTICULO, exchange.getArticulo(), "Getter/Setter de Artículo falló.");
        assertNull(exchange.getPassId(), "Pass ID debe ser nulo si no se setea.");
    }

    // -------------------------------------------------------------------------
    // Test 3: Método de Fábrica estático (crear)
    // -------------------------------------------------------------------------

    @Test
    void crear_shouldInitializeAllFieldsAndSetCurrentTime() {
        // When
        Exchange exchange = Exchange.crear(
                PASS_ID, PARTICIPANT_ID, FREEBIE_ID, ARTICULO, PUNTOS_REDUCIDOS
        );

        // Then
        assertNotNull(exchange, "El objeto creado no debe ser nulo.");

        // Verificar mapeo de campos
        assertEquals(PASS_ID, exchange.getPassId());
        assertEquals(PARTICIPANT_ID, exchange.getParticipantId());
        assertEquals(FREEBIE_ID, exchange.getFreebieId());
        assertEquals(PUNTOS_REDUCIDOS, exchange.getPuntosReducidos());

        // Verificar que la fecha se haya inicializado
        assertNotNull(exchange.getFechaIntercambio(), "La fecha de intercambio no debe ser nula.");

        // Verificar que el ID se mantenga nulo (será generado por MongoDB al guardar)
        assertNull(exchange.getId(), "El ID debe ser nulo antes de guardarse en la base de datos.");
    }
}

