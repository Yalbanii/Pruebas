package com.proyecto.congreso.notification.repository;

import com.proyecto.congreso.notification.model.Notification;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("!test")
public interface NotificationRepository extends MongoRepository<Notification, String> {


    // Derived query methods
    List<Notification> findByParticipantId(Long participantId);

    List<Notification> findByStatus(Notification.NotificationStatus status);

    List<Notification> findByType(Notification.NotificationType type);

    List<Notification> findByChannel(Notification.NotificationChannel channel);

    List<Notification> findByParticipantIdAndStatus(Long participantId, Notification.NotificationStatus status);

    List<Notification> findByParticipantIdOrderByCreatedAtDesc(Long participantId);

    // Custom query methods with @Query
    @Query("{ 'participantId': ?0, 'type': ?1 }")
    List<Notification> findByParticipantIdAndType(Long participantId, Notification.NotificationType type);

    @Query("{ 'status': ?0, 'createdAt': { $gte: ?1 } }")
    List<Notification> findPendingNotificationsAfter(Notification.NotificationStatus status, LocalDateTime afterDate);

    @Query("{ 'participantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Notification> findByParticipantIdAndDateRange(Long participantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "{ 'status': ?0 }", count = true)
    long countByStatus(Notification.NotificationStatus status);

    @Query("{ 'passId': ?0 }")
    List<Notification> findByPassId(Long passId);
}
