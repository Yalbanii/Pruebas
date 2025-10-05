package com.proyecto.congreso.pases.batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para ejecutar batch jobs manualmente.
 */
@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.enabled", havingValue = "true", matchIfMissing = true)

public class BatchJobController {

    private final JobLauncher jobLauncher;

    @Qualifier("processAssistancePointsJob")
    private final Job processAssistancePointsJob;

    @PostMapping("/process-assistances")
    public ResponseEntity<Map<String, Object>> runAssistancePointsJob() {
        log.info("🚀 Manual trigger: Process Assistance Points Job");

        try {
            // Parámetros únicos para cada ejecución
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("timestamp", LocalDateTime.now().toString())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(processAssistancePointsJob, jobParameters);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Assistance points job ejecutado exitosamente");
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "STARTED");
            response.put("description", "Procesando asistencias pendientes y sumando puntos");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error ejecutando Assistance Points Job", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to execute assistance points job");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}