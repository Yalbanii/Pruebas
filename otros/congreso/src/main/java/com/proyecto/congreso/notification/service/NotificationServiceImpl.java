package com.proyecto.congreso.notification.service;

import com.proyecto.congreso.shared.eventos.ParticipantCreatedEvent;
import com.proyecto.congreso.shared.eventos.PassAdquiredEvent;
import com.proyecto.congreso.notification.model.Notification;
import com.proyecto.congreso.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(Notification notification) {
        log.info("Creating notification for participant: {}", notification.getParticipantId());

        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }

        if (notification.getStatus() == null) {
            notification.setStatus(Notification.NotificationStatus.PENDING);
        }

        return notificationRepository.save(notification);
    }

    @Override
    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByParticipantId(Long participantId) {
        return notificationRepository.findByParticipantId(participantId);
    }

    @Override
    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new IllegalArgumentException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
        log.info("Deleted notification: {}", id);
    }

    @Override
    public List<Notification> getNotificationsByStatus(Notification.NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    @Override
    public List<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }

    @Override
    public List<Notification> getNotificationsByChannel(Notification.NotificationChannel channel) {
        return notificationRepository.findByChannel(channel);
    }

    @Override
    public List<Notification> getNotificationsByParticipantIdAndStatus(Long participantId, Notification.NotificationStatus status) {
        return notificationRepository.findByParticipantIdAndStatus(participantId, status);
    }

    @Override
    public List<Notification> getNotificationsByParticipantIdOrderByDate(Long participantId) {
        return notificationRepository.findByParticipantIdOrderByCreatedAtDesc(participantId);
    }

    @Override
    public List<Notification> getNotificationsByParticipantIdAndType(Long participantId, Notification.NotificationType type) {
        return notificationRepository.findByParticipantIdAndType(participantId, type);
    }

    @Override
    public List<Notification> getPendingNotificationsAfter(LocalDateTime afterDate) {
        return notificationRepository.findPendingNotificationsAfter(Notification.NotificationStatus.PENDING, afterDate);
    }

    @Override
    public List<Notification> getNotificationsByDateRange(Long participantId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByParticipantIdAndDateRange(participantId, startDate, endDate);
    }

    @Override
    public List<Notification> getNotificationsByPassId(Long passId) {
        return notificationRepository.findByPassId(passId);
    }

    @Override
    public long countByStatus(Notification.NotificationStatus status) {
        return notificationRepository.countByStatus(status);
    }

    @Override
    public void sendNotification(String notificationId) {
        Notification notification = getNotificationById(notificationId);

        if (notification.getStatus() == Notification.NotificationStatus.SENT) {
            log.warn("Notification {} already sent", notificationId);
            return;
        }

        try {
            // Simulate sending notification based on channel
            boolean sent = simulateSendNotification(notification);

            if (sent) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("Notification sent successfully: {} via {}", notificationId, notification.getChannel());
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send notification");
                log.error("Failed to send notification: {}", notificationId);
            }
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Error sending notification: {}", notificationId, e);
        }

        notificationRepository.save(notification);
    }

    @Override
    public void sendPendingNotifications() {
        List<Notification> pendingNotifications = getNotificationsByStatus(Notification.NotificationStatus.PENDING);
        log.info("Sending {} pending notifications", pendingNotifications.size());

        for (Notification notification : pendingNotifications) {
            sendNotification(notification.getId());
        }
    }

    @Override
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = getNotificationsByStatus(Notification.NotificationStatus.FAILED);
        log.info("Retrying {} failed notifications", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            notification.setStatus(Notification.NotificationStatus.RETRY);
            notificationRepository.save(notification);
            sendNotification(notification.getId());
        }
    }

    @Override
    public void notifyPassCreated(Long participantId, String participantEmail, Long passId, String passType) {
        Notification notification = new Notification(
                participantId,
                participantEmail,
                Notification.NotificationType.PASS_CREATED,
                Notification.NotificationChannel.EMAIL,
                "Pase Creado Exitosamente",
                String.format("Su Pase %s de tipo %s ha sido inscrito exitosamente.", passId, passType)
        );
        notification.setPassId(passId);

        createNotification(notification);
        notification.setMovementType("PASS_CREATED.");
        sendNotification(notification.getId());
    }

    @Override
    public void notifyAdd(Long participantId, String participantEmail, Long passId, Integer puntos) {
        Notification notification = new Notification(
                participantId,
                participantEmail,
                Notification.NotificationType.ASSISTANCE,
                Notification.NotificationChannel.EMAIL,
                "Puntos agregados",
                String.format("Se ha realizado un aumento de %s en su pase %s.", puntos, passId)
        );
        notification.setPassId(passId);
        notification.setPuntos(puntos);

        createNotification(notification);
        notification.setMovementType("ADD_POINTS.");
        sendNotification(notification.getId());
    }

    @Override
    public void notifyUse(Long participantId, String participantEmail, Long passId, Integer puntos) {
        Notification notification = new Notification(
                participantId,
                participantEmail,
                Notification.NotificationType.EXCHANGE,
                Notification.NotificationChannel.EMAIL,
                "Intercambio Realizado",
                String.format("Se ha realizado un intercambio de %s de su pase %s.", puntos, passId)
        );
        notification.setPassId(passId);
        notification.setPuntos(puntos);

        createNotification(notification);
        notification.setMovementType("USED_POINTS.");
        sendNotification(notification.getId());
    }


    @Override
    public void notifyPassClosed(Long participantId, String participantEmail, Long passId) {
        Notification notification = new Notification(
                participantId,
                participantEmail,
                Notification.NotificationType.PASS_CLOSED,
                Notification.NotificationChannel.EMAIL,
                "Pase Cerrado",
                String.format("Su pase %s ha sido cerrado exitosamente.", passId)
        );
        notification.setPassId(passId);

        createNotification(notification);
        notification.setMovementType("GAME_OVER.");
        sendNotification(notification.getId());
    }

    @Override
    public void notifyParticipantRegistered(Long participantId, String participantEmail, String participantName) {
        Notification notification = new Notification(
                participantId,
                participantEmail,
                Notification.NotificationType.PARTICIPANT_REGISTERED,
                Notification.NotificationChannel.PUSH,
                "Bienvenido al Congreso Interactivo",
                String.format("Bienvenido %s, su inscripcion ha sido exitosa. Puede comenzar a acumular puntos y desbloquear los logros del congreso", participantName)
        );
        notification.setMovementType("CUSTOMER_REGISTERED");

        createNotification(notification);
        sendNotification(notification.getId());
    }

    @Override
    public void notifyParticipantUpdated(Long participantId, String participantEmail, String participantName) {
        Notification notification = new Notification(
                participantId,
                participantEmail,
                Notification.NotificationType.PARTICIPANT_UPDATED,
                Notification.NotificationChannel.EMAIL,
                "Información Actualizada",
                String.format("Hola %s, su información ha sido actualizada exitosamente.", participantName)
        );
        notification.setMovementType("PARTICIPANT_UPDATED");

        createNotification(notification);
        sendNotification(notification.getId());
    }

    /**
     * Simulates sending a notification based on the channel.
     * In a real application, this would integrate with email services (SendGrid, AWS SES),
     * SMS services (Twilio), or push notification services (Firebase).
     *
     * @param notification The notification to send
     * @return true if simulation succeeds, false otherwise
     */
    private boolean simulateSendNotification(Notification notification) {
        // Simulate sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        // Simulate sending based on channel with polimorfismo
        return switch (notification.getChannel()) {
            case EMAIL -> simulateEmailSend(notification);
            case SMS -> simulateSmsSend(notification);
            case PUSH -> simulatePushSend(notification);
            case IN_APP -> simulateInAppSend(notification);
        };
    }

    private boolean simulateEmailSend(Notification notification) {
        log.info("📧 EMAIL sent to {}: {}", notification.getParticipantEmail(), notification.getSubject());
        return true;
    }

    private boolean simulateSmsSend(Notification notification) {
        log.info("📱 SMS sent: {}", notification.getMessage());
        return true;
    }

    private boolean simulatePushSend(Notification notification) {
        log.info("🔔 PUSH notification sent: {}", notification.getSubject());
        return true;
    }

    private boolean simulateInAppSend(Notification notification) {
        log.info("💬 IN-APP notification created: {}", notification.getMessage());
        return true;
    }

    // Event Listeners
    @EventListener
    @ApplicationModuleListener
    public void handleParticipantCreated(ParticipantCreatedEvent event) {
        log.debug("Handling ParticipantCreatedEvent for Participant: {}", event.getEmail());
        notifyParticipantRegistered(event.getParticipantId(), event.getEmail(), event.getFullName());
    }
    @EventListener
    @ApplicationModuleListener
    public void handlePassCreated(PassAdquiredEvent event) {
        log.debug("Handling PassAdquiredEvent for Pass: {}", event.getPassId());
        notifyPassCreated(
                event.getParticipantId(),
                event.getParticipantEmail(),
                event.getPassId(),
                event.getPassType()
        );
    }

}
