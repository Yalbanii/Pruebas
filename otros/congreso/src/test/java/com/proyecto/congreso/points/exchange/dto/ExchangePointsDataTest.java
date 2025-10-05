package com.proyecto.congreso.points.exchange.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExchangePointsDataTest {
    private static final String EXCHANGE_ID = "E-999";
    private static final Long PASS_ID = 101L;
    private static final String FREEBIE_ID = "F007";
    private static final Integer COSTO = 15;
    private static final Integer BALANCE_ACTUAL = 100;

    // -------------------------------------------------------------------------
    // Test 1: Constructor Específico de Negocio (5 parámetros)
    // -------------------------------------------------------------------------

    @Test
    void specificConstructor_shouldCalculateNewBalanceCorrectly() {
        // Given
        // El constructor de 5 parámetros debe calcular automáticamente newBalance
        ExchangePointsData data = new ExchangePointsData(
                EXCHANGE_ID,
                PASS_ID,
                FREEBIE_ID,
                COSTO,
                BALANCE_ACTUAL
        );

        // Expected
        Integer expectedNewBalance = BALANCE_ACTUAL - COSTO; // 100 - 15 = 85

        // Then
        assertNotNull(data, "El objeto no debe ser nulo.");
        assertEquals(PASS_ID, data.getPassId(), "El ID del Pass debe coincidir.");
        assertEquals(COSTO, data.getCosto(), "El costo debe coincidir.");
        assertEquals(BALANCE_ACTUAL, data.getCurrentBalance(), "El balance actual debe coincidir.");

        // Verificación clave del cálculo
        assertEquals(expectedNewBalance, data.getNewBalance(), "El nuevo balance debe ser calculado como Actual - Costo.");
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Completo (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // Given
        Integer newBalanceManual = 50;
        ExchangePointsData data = new ExchangePointsData(
                "E-1000",
                202L,
                "F001",
                10,
                60,
                newBalanceManual // Pasado manualmente, sin cálculo
        );

        // Then
        assertNotNull(data, "El objeto no debe ser nulo.");
        assertEquals(202L, data.getPassId());
        assertEquals(60, data.getCurrentBalance());
        assertEquals(newBalanceManual, data.getNewBalance(), "El balance nuevo debe ser el valor manual pasado.");
    }

    // -------------------------------------------------------------------------
    // Test 3: Constructor Vacío (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        ExchangePointsData data = new ExchangePointsData();
        Integer finalBalance = 75;

        // When
        data.setExchangeId(EXCHANGE_ID);
        data.setPassId(PASS_ID);
        data.setNewBalance(finalBalance);

        // Then
        assertEquals(EXCHANGE_ID, data.getExchangeId(), "Getter/Setter de ID falló.");
        assertEquals(PASS_ID, data.getPassId(), "Getter/Setter de PassId falló.");
        assertEquals(finalBalance, data.getNewBalance(), "Getter/Setter de NewBalance falló.");
        assertNull(data.getCosto(), "Costo debe ser nulo si no se setea.");
    }
}
