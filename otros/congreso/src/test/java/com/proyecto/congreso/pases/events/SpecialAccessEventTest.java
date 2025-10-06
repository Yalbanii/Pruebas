package com.proyecto.congreso.pases.events;
import com.proyecto.congreso.pases.events.SpecialAccessEvent;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialAccessEventTest {

    private static final Long PASS_ID = 100L;
    private static final Long PARTICIPANT_ID = 50L;
    private static final String FULL_NAME = "Alice Smith";
    private static final String EMAIL = "alice@example.com";
    private static final String CUSTOM_ACCESS_ID = "CUSTOM-001";

    // -------------------------------------------------------------------------
    // Test 1: Constructor Canónico (Completo)
    // -------------------------------------------------------------------------

    @Test
    void canonicalConstructor_shouldSetAllFieldsCorrectly() {
        // Given
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 5, 10, 30);

        // When
        SpecialAccessEvent event = new SpecialAccessEvent(
                PASS_ID, PARTICIPANT_ID, FULL_NAME, EMAIL, CUSTOM_ACCESS_ID, fixedTime
        );

        // Then
        assertEquals(PASS_ID, event.passId());
        assertEquals(PARTICIPANT_ID, event.participantId());
        assertEquals(FULL_NAME, event.fullName());
        assertEquals(EMAIL, event.email());
        assertEquals(CUSTOM_ACCESS_ID, event.accessId());
        assertEquals(fixedTime, event.createdAt());
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Secundario (4 Parámetros con Lógica de Negocio)
    // -------------------------------------------------------------------------

    @Test
    void secondaryConstructor_shouldGenerateIdAndTimestamp() throws InterruptedException {
        // Given
        // Capturamos el tiempo ANTES de la ejecución
        LocalDateTime timeBefore = LocalDateTime.now().minusSeconds(1);

        // When
        // Se llama al constructor que activa la lógica de generación
        SpecialAccessEvent event = new SpecialAccessEvent(
                PASS_ID, PARTICIPANT_ID, FULL_NAME, EMAIL
        );

        // Capturamos el tiempo DESPUÉS de la ejecución
        LocalDateTime timeAfter = LocalDateTime.now().plusSeconds(1);

        // Then
        // 1. Verificación de campos pasados
        assertEquals(PASS_ID, event.passId());
        assertEquals(PARTICIPANT_ID, event.participantId());

        // 2. Verificación de la Fecha (debe estar entre los dos puntos de tiempo)
        LocalDateTime eventTime = event.createdAt();
        assertNotNull(eventTime, "La fecha de creación no debe ser nula.");
        assertTrue(eventTime.isAfter(timeBefore) || eventTime.isEqual(timeBefore),
                "La fecha del evento debe ser posterior o igual al tiempo de inicio.");
        assertTrue(eventTime.isBefore(timeAfter) || eventTime.isEqual(timeAfter),
                "La fecha del evento debe ser anterior o igual al tiempo de finalización.");


        // 3. Verificación del Access ID (Lógica de formato)
        String accessId = event.accessId();
        assertNotNull(accessId, "El Access ID debe ser generado.");
        assertTrue(accessId.startsWith("ACCESS-"), "El ID debe empezar con 'ACCESS-'.");

        // Verificar que contenga el passId y participantId
        assertTrue(accessId.contains("-" + PASS_ID + "-"));
        assertTrue(accessId.contains("-" + PARTICIPANT_ID + "-"));

        // Verificar que tiene 4 segmentos (ACCESS-Pass-Participant-Timestamp)
        assertEquals(4, accessId.split("-").length);
    }
}