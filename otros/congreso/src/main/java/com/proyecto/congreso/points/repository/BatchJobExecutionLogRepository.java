package com.proyecto.congreso.points.repository;

import com.proyecto.congreso.points.model.BatchJobExecutionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatchJobExecutionLogRepository extends MongoRepository<BatchJobExecutionLog, String> {
    List<BatchJobExecutionLog> findByJobName(String jobName);

    List<BatchJobExecutionLog> findByStatus(String status);

    List<BatchJobExecutionLog> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    BatchJobExecutionLog findByJobExecutionId(Long jobExecutionId);
}
