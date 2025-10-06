package com.proyecto.congreso.pases.batch.dto;

import com.proyecto.congreso.pases.model.Pass;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class PassPointsDataTest {

    private static final Long PASS_ID = 456L;
    private static final Pass.PassType PASS_TYPE = Pass.PassType.GENERAL;
    private static final Integer ORIGINAL_BALANCE = 100;
    private static final Integer ADD_POINTS = 50;
    private static final Integer USE_POINTS = 20;
    private static final Integer NEW_BALANCE = 130;
    private static final Long PARTICIPANT_ID = 789L;
    private static final String EMAIL = "part@example.com";
    private static final LocalDateTime CALC_AT = LocalDateTime.of(2025, 10, 5, 12, 0);

    // -------------------------------------------------------------------------
    // Test 1: Constructor Completo (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // When
        PassPointsData data = new PassPointsData(
                PASS_ID, PASS_TYPE, ORIGINAL_BALANCE, ADD_POINTS, USE_POINTS, NEW_BALANCE,
                PARTICIPANT_ID, EMAIL, CALC_AT
        );

        // Then
        assertNotNull(data);
        assertEquals(PASS_ID, data.getPassId());
        assertEquals(PASS_TYPE, data.getPassType());
        assertEquals(ORIGINAL_BALANCE, data.getOriginalPointsBalance());
        assertEquals(NEW_BALANCE, data.getNewPointsBalance());
        assertEquals(EMAIL, data.getParticipantEmail());
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Vacío (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        PassPointsData data = new PassPointsData();

        // When
        data.setParticipantId(PARTICIPANT_ID);
        data.setAddPoints(ADD_POINTS);
        data.setCalculatedAt(CALC_AT);

        // Then
        assertEquals(PARTICIPANT_ID, data.getParticipantId());
        assertEquals(ADD_POINTS, data.getAddPoints());
        assertEquals(CALC_AT, data.getCalculatedAt());
        assertNull(data.getPassId(), "Pass ID debe ser nulo si no se setea.");
    }

    // -------------------------------------------------------------------------
    // Test 3: Constructor con Pass y Puntos (Lógica de Negocio Contradictoria)
    // -------------------------------------------------------------------------

    @Test
    void secondaryConstructor_withPassAndPoints_shouldUseFinalAssignment() {
        // Given
        Pass mockPass = new Pass();
        Integer pointsChange = 10;
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 5, 15, 0);

        // ⚠️ La lógica del constructor es:
        // 1. this.newPointsBalance = originalPointsBalance + points; (50 + 10 = 60)
        // 2. this.newPointsBalance = originalPointsBalance - points; (50 - 10 = 40)
        // El valor final esperado es 40.
        Integer expectedFinalBalance = mockPass.getPointsBalance() - pointsChange; // 50 - 10 = 40

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            // When
            PassPointsData data = new PassPointsData(mockPass, pointsChange);

            // Then
            assertNotNull(data);

            // Verificar asignaciones directas de Pass
            assertEquals(mockPass.getPassId(), data.getPassId());
            assertEquals(mockPass.getPointsBalance(), data.getOriginalPointsBalance());
            assertEquals(fixedTime, data.getCalculatedAt(), "La fecha debe ser la mockeada.");

            // Verificar la última asignación de puntos
            assertEquals(pointsChange, data.getUsePoints(), "UsePoints debe ser igual a pointsChange.");
            assertEquals(expectedFinalBalance, data.getNewPointsBalance(),
                    "El balance final debe ser 40 (dada la sobrescritura 'original - points').");
        }
    }

    // -------------------------------------------------------------------------
    // Test 4: Constructor Placeholder
    // -------------------------------------------------------------------------

    @Test
    void placeholderConstructor_shouldCreateObjectButMayHaveNullFields() {
        // When
        PassPointsData data = new PassPointsData(PASS_ID, ORIGINAL_BALANCE, 5);

        // Then
        assertNotNull(data);
        // Dado que el constructor está vacío en la clase original:
        assertNull(data.getPassId(), "Los campos deben ser nulos si el constructor no los asigna.");
        assertNull(data.getOriginalPointsBalance());
    }
}