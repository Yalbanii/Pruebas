package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.SpecialAccess;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecialAccessResponseTest {

    private static final Long ACCESS_ID = 1L;
    private static final Long PASS_ID = 1001L;
    private static final Long PARTICIPANT_ID = 500L;
    private static final String EMAIL = "juan@example.com";
    private static final String NAME = "Juan Pérez";
    private static final Integer POINTS = 20;
    private static final String CODE = "ACCESS-XYZ";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 10, 5, 10, 0);

    // -------------------------------------------------------------------------
    // Test 1: Constructor Completo (@AllArgsConstructor) y Getters/Setters
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // Given
        SpecialAccessResponse response = new SpecialAccessResponse(
                ACCESS_ID, PASS_ID, PARTICIPANT_ID, EMAIL, NAME, POINTS, true, CODE, CREATED_AT
        );

        // Then
        assertNotNull(response, "El objeto no debe ser nulo.");
        assertEquals(ACCESS_ID, response.getAccessId(), "El Access ID no coincide.");
        assertEquals(EMAIL, response.getParticipantEmail(), "El email no coincide.");
        assertTrue(response.getReached(), "El estado 'reached' debe ser verdadero.");
        assertEquals(POINTS, response.getPointsAchieved(), "Los puntos no coinciden.");
    }

    @Test
    void noArgsConstructor_andSetters_shouldWorkCorrectly() {
        // Given
        SpecialAccessResponse response = new SpecialAccessResponse();

        // When
        response.setPassId(PASS_ID);
        response.setParticipantName(NAME);
        response.setReached(false);

        // Then
        assertEquals(PASS_ID, response.getPassId(), "Getter/Setter de PassId falló.");
        assertEquals(NAME, response.getParticipantName(), "Getter/Setter de Name falló.");
        assertFalse(response.getReached(), "Getter/Setter de Reached falló.");
        assertNull(response.getAccessId(), "Access ID debe ser nulo si no se setea.");
    }

    // -------------------------------------------------------------------------
    // Test 2: Mapeo (fromEntity)
    // -------------------------------------------------------------------------

    @Test
    void fromEntity_shouldMapSpecialAccessEntityCorrectly() {
        // Given
        // Simulamos una entidad SpecialAccess usando Mockito
        SpecialAccess mockAccess = mock(SpecialAccess.class);

        when(mockAccess.getAccessId()).thenReturn(ACCESS_ID);
        when(mockAccess.getPassId()).thenReturn(PASS_ID);
        when(mockAccess.getParticipantEmail()).thenReturn(EMAIL);
        when(mockAccess.getPointsAchieved()).thenReturn(POINTS);
        when(mockAccess.getReached()).thenReturn(true);
        when(mockAccess.getCreatedAt()).thenReturn(CREATED_AT);

        // When
        SpecialAccessResponse response = SpecialAccessResponse.fromEntity(mockAccess);

        // Then
        assertNotNull(response);
        assertEquals(ACCESS_ID, response.getAccessId());
        assertEquals(PASS_ID, response.getPassId());
        assertEquals(EMAIL, response.getParticipantEmail());
        assertEquals(POINTS, response.getPointsAchieved());
        assertTrue(response.getReached());
        assertEquals(CREATED_AT, response.getCreatedAt());
    }
}