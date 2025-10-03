package com.proyecto.congreso.notification.service;

import com.proyecto.congreso.notification.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {

    // CRUD operations
    Notification createNotification(Notification notification);

    Notification getNotificationById(String id);

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByParticipantId(Long participant);

    void deleteNotification(String id);

    // Query operations
    List<Notification> getNotificationsByStatus(Notification.NotificationStatus status);

    List<Notification> getNotificationsByType(Notification.NotificationType type);

    List<Notification> getNotificationsByChannel(Notification.NotificationChannel channel);

    List<Notification> getNotificationsByParticipantIdAndStatus(Long participantId, Notification.NotificationStatus status);

    List<Notification> getNotificationsByParticipantIdOrderByDate(Long participantId);

    List<Notification> getNotificationsByParticipantIdAndType(Long participantId, Notification.NotificationType type);

    List<Notification> getPendingNotificationsAfter(LocalDateTime afterDate);

    List<Notification> getNotificationsByDateRange(Long participantId, LocalDateTime startDate, LocalDateTime endDate);

    List<Notification> getNotificationsByPassId(Long passId);

    long countByStatus(Notification.NotificationStatus status);

    // Notification sending operations
    void sendNotification(String notificationId);

    void sendPendingNotifications();

    void retryFailedNotifications();

    // Business operations - Account events
    void notifyPassCreated(Long participantId, String participantEmail, Long passIdr, String passType);

    void notifyAdd(Long participantId, String participantEmail, Long passId, Integer puntos);

    void notifyUse(Long participantId, String participantEmail, Long passId, Integer puntos);


    void notifyPassClosed(Long participantId, String participantEmail, Long passId);

    // Business operations - Customer events
    void notifyParticipantRegistered(Long participantId, String participantEmail, String participantName);

    void notifyParticipantUpdated(Long participantId, String participantEmail, String participantName);

}
