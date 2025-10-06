package com.proyecto.congreso.notification.service;

import com.proyecto.congreso.points.assistance.events.AssistanceRegisteredEvent;
import com.proyecto.congreso.notification.model.MovementPointsLog;
import com.proyecto.congreso.notification.repository.MovementPointsLogRepository;
import com.proyecto.congreso.points.exchange.events.ExchangeRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class MovementPointsServiceImpl implements MovementPointsLogService{

    private final MovementPointsLogRepository movementPointsLogRepository;

    @Override
    public MovementPointsLog createMovementPointsLog(MovementPointsLog transactionLog) {
        log.info("Creating MovementPointsLog for Pass: {}", transactionLog.getPassId());

        if (transactionLog.getTimestamp() == null) {
            transactionLog.setTimestamp(LocalDateTime.now());
        }

        return movementPointsLogRepository.save(transactionLog);
    }

    @Override
    public MovementPointsLog getMovementPointsLogById(String id) {
        return movementPointsLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction log not found with id: " + id));
    }

    @Override
    public List<MovementPointsLog> getAllMovementPointsLog() {
        return movementPointsLogRepository.findAll();
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByPassId(Long passId) {
        return movementPointsLogRepository.findByPassId(passId);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByMovementType(String movementType) {
        return movementPointsLogRepository.findByMovementType(movementType);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByParticipantId(Long participantId) {
        return movementPointsLogRepository.findByParticipantId(participantId);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByDateRange(LocalDateTime start, LocalDateTime end) {
        return movementPointsLogRepository.findByTimestampBetween(start, end);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByPassIdAndDateRange(Long passId, LocalDateTime start, LocalDateTime end) {
        return movementPointsLogRepository.findByPassIdOrderByTimestampDesc(passId);
    }


    @Override
    public long countByMovementType(String movementType) {
        return movementPointsLogRepository.countByMovementType(movementType);
    }

    @Override
    public long countByPassId(Long passId) {
        return movementPointsLogRepository.countByPassId(passId);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByStatus(String status) {
        return movementPointsLogRepository.findByStatus(status);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByPassIdOrderedByDate(Long passId) {
        return movementPointsLogRepository.findByPassIdOrderByTimestampDesc(passId);
    }

    @Override
    public List<MovementPointsLog> getMovementPointsLogByPoints(Integer puntos) {
        return movementPointsLogRepository.findByPoints(puntos);
    }

    @Override
    public long countByStatus(String status) {
        return movementPointsLogRepository.countByStatus(status);
    }

    @Override
    public void deleteMovementPointsLog(String id) {
        if (!movementPointsLogRepository.existsById(id)) {
            throw new IllegalArgumentException("Movement points log not found with id: " + id);
        }
        movementPointsLogRepository.deleteById(id);
        log.info("Deleted movement log: {}", id);
    }

    // Event Listeners
    @ApplicationModuleListener
    public void handleMovementCompleted(AssistanceRegisteredEvent event) {
        log.debug("Logging AssistanceRegisteredEvent for Pass: {}", event.getPassId());

        MovementPointsLog log = MovementPointsLog.builder()
                .passId(event.getPassId())
                .movementType("ADD")
                .balancePoints(event.getAmountPoints())
                .timestamp(event.getTimestamp())
                .description(String.format("%s movement add you points: %s", event.getAmountPoints()))
                .status("SUCCESS")
                .build();

        createMovementPointsLog(log);
    }


    @ApplicationModuleListener
    public void handleUsePoints(ExchangeRegisteredEvent event) {
        log.debug("Use of points in ExchangeRegisteredEvent for Pass: {}", event.getPassId());

        MovementPointsLog log = MovementPointsLog.builder()
                .movementId(java.util.UUID.randomUUID().toString())
                .passId(event.getPassId())
                .movementType("USE")
                .balancePoints(event.getCosto())
                .timestamp(event.getTimestamp())
                .description(String.format("Use of points: %s", event.getCosto()))
                .status("SUCCESS")
                .build();

        createMovementPointsLog(log);
    }
}
