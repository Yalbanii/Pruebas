package com.proyecto.congreso.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private Long participantId;
    private String participantEmail;
    private NotificationType type;
    private NotificationChannel channel;
    private String subject;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String errorMessage;

    // Metadata fields
    private Long passId;
    private String movementType;
    private Integer puntos;

    public Notification(Long participantId, String participantEmail, NotificationType notificationType, NotificationChannel notificationChannel, String paseCreadoExitosamente, String format) {
    }

    public enum NotificationType {
        PASS_CREATED,
        PASS_CLOSED,
        ASSISTANCE,
        EXCHANGE,
        CERTIFICATE_REACHED,
        SPECIAL_ACCESS_REACHED,
        PARTICIPANT_REGISTERED,
        PARTICIPANT_UPDATED
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        IN_APP
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED,
        RETRY
    }

    public Notification(Long participantId, String participantEmail, NotificationType type, NotificationChannel channel, String subject, String message, NotificationStatus status, LocalDateTime createdAt) {
        this.participantId = participantId;
        this.participantEmail = participantEmail;
        this.type = type;
        this.channel = channel;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }
}
