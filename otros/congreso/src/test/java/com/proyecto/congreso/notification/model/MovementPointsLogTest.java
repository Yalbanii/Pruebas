package com.proyecto.congreso.notification.model;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MovementPointsLogTest {

    private static final String ID = "ML-001";
    private static final String MOVEMENT_ID = "TXN-12345";
    private static final Long PARTICIPANT_ID = 500L;
    private static final Long PASS_ID = 101L;
    private static final String TYPE = "REDEEM";
    private static final Integer POINTS = 15;
    private static final Integer BALANCE = 85;
    private static final String STATUS = "SUCCESS";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2025, 10, 5, 12, 0);
    private static final String DESCRIPTION = "Intercambio de Freebie";
    private static final Map<String, Object> METADATA = Map.of("source", "ExchangeService");

    // -------------------------------------------------------------------------
    // Test 1: Builder Pattern (@Builder)
    // -------------------------------------------------------------------------

    @Test
    void builder_shouldCreateObjectWithAllFieldsSet() {
        // When
        MovementPointsLog log = MovementPointsLog.builder()
                .id(ID)
                .movementId(MOVEMENT_ID)
                .participantId(PARTICIPANT_ID)
                .passId(PASS_ID)
                .movementType(TYPE)
                .points(POINTS)
                .balancePoints(BALANCE)
                .status(STATUS)
                .timestamp(TIMESTAMP)
                .metadata(METADATA)
                .description(DESCRIPTION)
                .build();

        // Then
        assertNotNull(log, "El objeto no debe ser nulo.");
        assertEquals(ID, log.getId(), "El ID no coincide.");
        assertEquals(PASS_ID, log.getPassId(), "El Pass ID no coincide.");
        assertEquals(TYPE, log.getMovementType(), "El tipo de movimiento no coincide.");
        assertEquals(BALANCE, log.getBalancePoints(), "El balance no coincide.");
        assertEquals(METADATA.get("source"), log.getMetadata().get("source"), "El metadato no coincide.");
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Vacío (@NoArgsConstructor) y Setters
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        MovementPointsLog log = new MovementPointsLog();

        // When
        log.setParticipantId(PARTICIPANT_ID);
        log.setPoints(POINTS);
        log.setMovementType(TYPE);

        // Then
        assertEquals(PARTICIPANT_ID, log.getParticipantId(), "Getter/Setter de Participant ID falló.");
        assertEquals(POINTS, log.getPoints(), "Getter/Setter de Points falló.");
        assertEquals(TYPE, log.getMovementType(), "Getter/Setter de Type falló.");
        assertNull(log.getId(), "El ID debe ser nulo si no se setea.");
        assertNull(log.getMetadata(), "Metadata debe ser nulo si no se setea.");
    }

    // -------------------------------------------------------------------------
    // Test 3: Constructor Completo (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFields() {
        // Given
        MovementPointsLog log = new MovementPointsLog(
                ID, MOVEMENT_ID, PARTICIPANT_ID, PASS_ID, TYPE, POINTS, BALANCE,
                STATUS, TIMESTAMP, METADATA, DESCRIPTION
        );

        // Then
        assertNotNull(log);
        assertEquals(DESCRIPTION, log.getDescription());
        assertEquals(STATUS, log.getStatus());
        assertEquals(TIMESTAMP, log.getTimestamp());
    }
}






