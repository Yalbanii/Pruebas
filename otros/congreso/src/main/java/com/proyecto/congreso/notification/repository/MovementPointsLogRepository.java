package com.proyecto.congreso.notification.repository;

import com.proyecto.congreso.notification.model.MovementPointsLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@org.springframework.context.annotation.Profile("!test")
public interface MovementPointsLogRepository extends MongoRepository<MovementPointsLog, String> {

    List<MovementPointsLog> findByPassId(Long passId);

    List<MovementPointsLog> findByMovementType(String movementType);

    List<MovementPointsLog> findByParticipantId(Long participantId);

    List<MovementPointsLog> findByStatus(String status);

    List<MovementPointsLog> findByPassIdOrderByTimestampDesc(Long passId);

    @Query("{ 'timestamp' : { $gte: ?0, $lte: ?1 } }")
    List<MovementPointsLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("{ 'passId' : ?0, 'timestamp' : { $gte: ?1, $lte: ?2 } }")
    List<MovementPointsLog> findByPassIdAndTimestampBetween(Long passId, LocalDateTime start, LocalDateTime end);

    @Query("{ 'participantId' : ?0, 'timestamp' : { $gte: ?1, $lte: ?2 } }")
    List<MovementPointsLog> findByParticipantIdAndTimestampBetween(Long participantId, LocalDateTime start, LocalDateTime end);

    @Query("{ 'points' : { $gte: ?0, $lte: ?1 } }")
    List<MovementPointsLog> findByPoints(Integer points);

    long countByMovementType(String movementType);

    long countByPassId(Long passId);

    long countByStatus(String status);
}