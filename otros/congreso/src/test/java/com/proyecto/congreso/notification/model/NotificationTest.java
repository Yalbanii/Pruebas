package com.proyecto.congreso.notification.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
public class NotificationTest {

    private static final String ID = "N-MONGO-101";
    private static final Long PARTICIPANT_ID = 900L;
    private static final String EMAIL = "test@example.com";
    private static final Notification.NotificationType TYPE = Notification.NotificationType.PASS_CREATED;
    private static final Notification.NotificationChannel CHANNEL = Notification.NotificationChannel.EMAIL;
    private static final String SUBJECT = "Pase Creado Exitosamente";
    private static final String MESSAGE = "Su pase 101 ha sido activado.";
    private static final Notification.NotificationStatus STATUS = Notification.NotificationStatus.PENDING;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 10, 5, 12, 0);
    private static final String ERROR_MESSAGE = "Error de conexión SMTP";

    // Metadata
    private static final Long PASS_ID = 101L;
    private static final String MOVEMENT_TYPE = "CREACION";
    private static final Integer PUNTOS = 50;

    // -------------------------------------------------------------------------
    // Test 1: Constructor Canónico (@AllArgsConstructor)
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // Given
        LocalDateTime sentTime = LocalDateTime.of(2025, 10, 6, 10, 0);

        // When
        Notification notification = new Notification(
                ID, PARTICIPANT_ID, EMAIL, TYPE, CHANNEL, SUBJECT, MESSAGE, STATUS,
                CREATED_AT, sentTime, ERROR_MESSAGE, PASS_ID, MOVEMENT_TYPE, PUNTOS
        );

        // Then
        assertNotNull(notification);
        assertEquals(ID, notification.getId());
        assertEquals(EMAIL, notification.getParticipantEmail());
        assertEquals(sentTime, notification.getSentAt(), "El campo sentAt debe coincidir.");
        assertEquals(ERROR_MESSAGE, notification.getErrorMessage(), "El mensaje de error debe coincidir.");
        assertEquals(PUNTOS, notification.getPuntos(), "El campo de metadato 'puntos' debe coincidir.");
    }

    // -------------------------------------------------------------------------
    // Test 2: Constructor Secundario (8 Parámetros)
    // -------------------------------------------------------------------------

    @Test
    void secondaryConstructor8Params_shouldSetBasicFields() {
        // When
        Notification notification = new Notification(
                PARTICIPANT_ID, EMAIL, TYPE, CHANNEL, SUBJECT, MESSAGE, STATUS, CREATED_AT
        );

        // Then
        assertNotNull(notification);
        assertEquals(PARTICIPANT_ID, notification.getParticipantId());
        assertEquals(Notification.NotificationStatus.PENDING, notification.getStatus());
        assertEquals(CREATED_AT, notification.getCreatedAt());

        // Campos de metadato/JPA deben ser nulos
        assertNull(notification.getId());
        assertNull(notification.getPassId());
    }

    // -------------------------------------------------------------------------
    // Test 4: Getters/Setters y Metadatos
    // -------------------------------------------------------------------------

    @Test
    void dataAnnotation_shouldAllowSettingAndGettingMetadata() {
        // Given
        Notification notification = new Notification();

        // When
        notification.setPassId(PASS_ID);
        notification.setMovementType(MOVEMENT_TYPE);
        notification.setSubject(SUBJECT);

        // Then
        assertEquals(PASS_ID, notification.getPassId(), "Getter/Setter de PassId falló.");
        assertEquals(MOVEMENT_TYPE, notification.getMovementType(), "Getter/Setter de MovementType falló.");
        assertEquals(SUBJECT, notification.getSubject(), "Getter/Setter de Subject falló.");
    }

    // -------------------------------------------------------------------------
    // Test 5: Enum Values
    // -------------------------------------------------------------------------

    @Test
    void enums_shouldContainExpectedValues() {
        // Tipos
        assertTrue(Notification.NotificationType.PASS_CREATED.name().equals("PASS_CREATED"));
        assertTrue(Notification.NotificationType.CERTIFICATE_REACHED.name().equals("CERTIFICATE_REACHED"));

        // Canales
        assertEquals(4, Notification.NotificationChannel.values().length);
        assertTrue(Notification.NotificationChannel.EMAIL.name().equals("EMAIL"));

        // Estados
        assertEquals(Notification.NotificationStatus.FAILED, Notification.NotificationStatus.valueOf("FAILED"));
    }
}

