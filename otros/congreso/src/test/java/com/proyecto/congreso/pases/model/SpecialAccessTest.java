package com.proyecto.congreso.pases.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialAccessTest {

    private static final Long ACCESS_ID = 1L;
    private static final Long PASS_ID = 1001L;
    private static final Long PARTICIPANT_ID = 500L;
    private static final String EMAIL = "pedro@example.com";
    private static final String NAME = "Pedro Gómez";
    private static final Integer POINTS = 30;
    private static final String CODE = "ACCESS-XYZ-123";

    // -------------------------------------------------------------------------
    // Test 1: Constructor Completo (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateEntityWithAllFieldsSet() {
        // Given
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 5, 12, 0);

        // When
        SpecialAccess access = new SpecialAccess(
                ACCESS_ID, PASS_ID, PARTICIPANT_ID, EMAIL, NAME, POINTS, false, fixedTime, CODE
        );

        // Then
        assertNotNull(access, "La entidad no debe ser nula.");
        assertEquals(ACCESS_ID, access.getAccessId(), "El Access ID no coincide.");
        assertEquals(NAME, access.getParticipantName(), "El nombre no coincide.");
        assertFalse(access.getReached(), "El estado 'reached' debe ser el valor pasado (false).");
        assertEquals(fixedTime, access.getCreatedAt(), "La fecha debe coincidir.");
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Vacío (@NoArgsConstructor) y Defaults
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldInitializeDefaultReachedValue() {
        // When
        SpecialAccess access = new SpecialAccess();

        // Then
        // El valor por defecto en la clase (Java) es 'true'.
        assertTrue(access.getReached(), "El valor 'reached' debe ser TRUE por defecto.");
        assertNull(access.getPassId(), "Los campos Integer/Long deben ser nulos.");
        assertNull(access.getCreatedAt(), "La fecha debe ser nula antes de que JPA la inicialice.");

        // Cuando se usa el setter, debe funcionar
        access.setPointsAchieved(POINTS);
        assertEquals(POINTS, access.getPointsAchieved());
    }

    // -------------------------------------------------------------------------
    // Test 3: Método de Fábrica estático (create)
    // -------------------------------------------------------------------------

    @Test
    void create_shouldInitializeMandatoryFieldsAndGenerateCode() {
        // When
        SpecialAccess access = SpecialAccess.create(
                PASS_ID, PARTICIPANT_ID, EMAIL, NAME, POINTS
        );

        // Then
        assertNotNull(access, "El objeto creado no debe ser nulo.");

        // 1. Verificar mapeo de campos
        assertEquals(PASS_ID, access.getPassId());
        assertEquals(PARTICIPANT_ID, access.getParticipantId());
        assertEquals(EMAIL, access.getParticipantEmail());
        assertEquals(POINTS, access.getPointsAchieved());

        // 2. Verificar lógica de negocio
        assertTrue(access.getReached(), "El campo 'reached' debe ser TRUE.");

        // 3. Verificar código de acceso (Generación)
        String accessCode = access.getAccessCode();
        assertNotNull(accessCode, "El código de acceso debe ser generado.");
        assertTrue(accessCode.startsWith("ACCESS-"), "El código debe empezar con 'ACCESS-'.");

        // Verificar que contenga los IDs clave
        assertTrue(accessCode.contains("-" + PASS_ID + "-"), "El código debe contener el Pass ID.");
        assertTrue(accessCode.contains("-" + PARTICIPANT_ID + "-"), "El código debe contener el Participant ID.");

        // 4. Verificar campos que serán manejados por JPA
        assertNull(access.getAccessId(), "El ID debe ser nulo antes de guardarse.");
        assertNull(access.getCreatedAt(), "La fecha debe ser nula antes de que JPA la inicialice.");
    }
}