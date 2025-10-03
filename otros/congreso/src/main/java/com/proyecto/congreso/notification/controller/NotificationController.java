package com.proyecto.congreso.notification.controller;

import com.proyecto.congreso.notification.model.Notification;
import com.proyecto.congreso.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Profile("!test")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/participants/{participantId}")
    public ResponseEntity<List<Notification>> getNotificationsByParticipantId(@PathVariable Long participantId) {
        List<Notification> notifications = notificationService.getNotificationsByParticipantId(participantId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/participants/{participantId}/ordered")
    public ResponseEntity<List<Notification>> getNotificationsByParticipantIdOrdered(@PathVariable Long participantId) {
        List<Notification> notifications = notificationService.getNotificationsByParticipantId(participantId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable Notification.NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable Notification.NotificationType type) {
        List<Notification> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<Notification>> getNotificationsByChannel(@PathVariable Notification.NotificationChannel channel) {
        List<Notification> notifications = notificationService.getNotificationsByChannel(channel);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/participants/{participantId}/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByParticipantIdAndStatus(
            @PathVariable Long participantId,
            @PathVariable Notification.NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByParticipantIdAndStatus(participantId, status);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/participants/{participantId}/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByParticipantIdAndType(
            @PathVariable Long participantId,
            @PathVariable Notification.NotificationType type) {
        List<Notification> notifications = notificationService.getNotificationsByParticipantIdAndType(participantId, type);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/pases/{passId}")
    public ResponseEntity<List<Notification>> getNotificationsByPassId(@PathVariable Long passId) {
        List<Notification> notifications = notificationService.getNotificationsByPassId(passId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/pending/after")
    public ResponseEntity<List<Notification>> getPendingNotificationsAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime afterDate) {
        List<Notification> notifications = notificationService.getPendingNotificationsAfter(afterDate);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/participants/{participantId}/daterange")
    public ResponseEntity<List<Notification>> getNotificationsByDateRange(
            @PathVariable Long participantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Notification> notifications = notificationService.getNotificationsByDateRange(participantId, startDate, endDate);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable Notification.NotificationStatus status) {
        long count = notificationService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // Notification sending operations
    @PostMapping("/{id}/send")
    public ResponseEntity<Void> sendNotification(@PathVariable String id) {
        notificationService.sendNotification(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-pending")
    public ResponseEntity<Void> sendPendingNotifications() {
        notificationService.sendPendingNotifications();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/retry-failed")
    public ResponseEntity<Void> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok().build();
    }
}
