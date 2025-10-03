package com.proyecto.congreso.notification.service;

import com.proyecto.congreso.notification.model.MovementPointsLog;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementPointsLogService {

    MovementPointsLog createMovementPointsLog(MovementPointsLog movementPointsLog);

    MovementPointsLog getMovementPointsLogById(String id);

    List<MovementPointsLog> getAllMovementPointsLog();

    List<MovementPointsLog> getMovementPointsLogByPassId(Long passId);

    List<MovementPointsLog> getMovementPointsLogByMovementType(String movementType);

    List<MovementPointsLog> getMovementPointsLogByParticipantId(Long participantId);

    List<MovementPointsLog> getMovementPointsLogByStatus(String status);

    List<MovementPointsLog> getMovementPointsLogByPassIdOrderedByDate(Long passId);

    List<MovementPointsLog> getMovementPointsLogByDateRange(LocalDateTime start, LocalDateTime end);

    List<MovementPointsLog> getMovementPointsLogByPassIdAndDateRange(Long passId, LocalDateTime start, LocalDateTime end);

    List<MovementPointsLog> getMovementPointsLogByPoints(Integer puntos);

    long countByMovementType(String movementType);

    long countByPassId(Long passId);

    long countByStatus(String status);

    void deleteMovementPointsLog(String id);
}
