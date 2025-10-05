package com.proyecto.congreso.pases.batch.config;

import com.proyecto.congreso.points.assistance.model.Asistencia;
import com.proyecto.congreso.points.assistance.repository.AsistenciaRepository;
import com.proyecto.congreso.points.assistance.dto.AsistenciaPointsData;
import com.proyecto.congreso.pases.batch.listener.BatchJobExecutionMongoListener;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

/**
 * Configuración de Spring Batch para el procesamiento diario de puntos.
 *
 * Job: addJob y useJob
 * Step 1: calculatePoints - Lee pases, calcula y agrega puntos.
 * Step 2: publishEventsStep - Publica eventos para logs en MongoDB
 Job: pointsJob
 * Step: calculateAndApplyPointsStep - Lee pases activos, calcula y publica eventos

**
        * ✅ CORREGIDO: Configuración de Spring Batch para procesamiento de asistencias.
        *
        * JOB: processAssistancePointsJob
 * - Lee asistencias pendientes desde MongoDB
 * - Suma puntos en Pass (MySQL)
 * - Marca asistencias como procesadas
 *
         * CASO DE USO:
        * - Procesar asistencias que se registraron pero no sumaron puntos
 *   (por fallos de red, timeouts, etc.)
        * - Ejecutar diariamente o bajo demanda
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.enabled", havingValue = "true", matchIfMissing = true)

public class PointsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PassRepository passRepository;
    private final AsistenciaRepository asistenciaRepository;

    @Autowired(required = false)
    private BatchJobExecutionMongoListener batchJobExecutionMongoListener;

    // ========== JOB DEFINITION ==========

    /**
     * Job que procesa asistencias pendientes y suma puntos.
     */
    @Bean
    public Job processAssistancePointsJob() {
        JobBuilder jobBuilder = new JobBuilder("processAssistancePointsJob", jobRepository);

        // Add MongoDB listener if available
        if (batchJobExecutionMongoListener != null) {
            jobBuilder.listener(batchJobExecutionMongoListener);
        }

        return jobBuilder
                .start(processAssistancesStep())
                .build();
    }

    // ========== STEP: PROCESS ASSISTANCES ==========

    @Bean
    public Step processAssistancesStep() {
        return new StepBuilder("processAssistancesStep", jobRepository)
                .<Asistencia, AsistenciaPointsData>chunk(10, transactionManager)  // ← CAMBIO: AsistenciaPointsData
                .reader(pendingAssistancesReader())
                .processor(assistancePointsProcessor())
                .writer(pointsUpdaterWriter())
                .build();
    }

    // ---------- READER: Lee asistencias pendientes de MongoDB -----------

    @Bean
    public RepositoryItemReader<Asistencia> pendingAssistancesReader() {
        return new RepositoryItemReaderBuilder<Asistencia>()
                .name("pendingAssistancesReader")
                .repository(asistenciaRepository)
                .methodName("findByStatus") // Busca asistencias "PENDIENTE"
                .arguments("PENDIENTE")
                .sorts(Collections.singletonMap("fechaAsistencia", Sort.Direction.ASC))
                .pageSize(10)
                .build();
    }

    // ---------- PROCESSOR: Valida Pass y prepara datos -----------

    @Bean
    public ItemProcessor<Asistencia, AsistenciaPointsData> assistancePointsProcessor() {
        return asistencia -> {
            log.info("📊 Procesando asistencia: Pass={}, Conferencia={}, Puntos={}",
                    asistencia.getPassId(),
                    asistencia.getConferenciaId(),
                    asistencia.getPuntosOtorgados());

            // Validar que el Pass existe y está activo
            Optional<Pass> optionalPass = passRepository.findById(asistencia.getPassId());
            if (optionalPass.isEmpty()) {
                log.error("❌ Pass ID {} no encontrado. Omitiendo asistencia.",
                        asistencia.getPassId());
                return null; // Skip this item
            }

            Pass pass = optionalPass.get();
            if (pass.getStatus() != Pass.PassStatus.ACTIVE) {
                log.warn("⚠️ Pass ID {} no está activo. Estado: {}. Omitiendo.",
                        pass.getPassId(), pass.getStatus());
                return null; // Skip this item
            }

            // Preparar datos para el writer
            return new AsistenciaPointsData(
                    asistencia.getId(),
                    asistencia.getPassId(),
                    asistencia.getConferenciaId(),
                    asistencia.getPuntosOtorgados(),
                    pass.getPointsBalance(),
                    pass.getPointsBalance() + asistencia.getPuntosOtorgados()
            );
        };
    }

    // ---------- WRITER: Suma puntos en MySQL y marca como procesada -----------

    @Bean
    public ItemWriter<AsistenciaPointsData> pointsUpdaterWriter() {
        return items -> {
            for (AsistenciaPointsData data : items) {
                if (data == null) continue;

                try {
                    // 1. Actualizar Pass en MySQL
                    Pass pass = passRepository.findById(data.getPassId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Pass not found: " + data.getPassId()));

                    Integer balanceAnterior = pass.getPointsBalance();
                    Integer nuevoBalance = balanceAnterior + data.getPointsAwarded();

                    pass.setPointsBalance(nuevoBalance);
                    pass.setPointsAdd(Pass.PointsMovementAdd.ADD);
                    pass.setUpdatedAt(LocalDateTime.now());

                    // Verificar logros
                    checkAchievements(pass, balanceAnterior, nuevoBalance);

                    passRepository.save(pass);

                    // 2. Marcar asistencia como PROCESADA en MongoDB
                    Asistencia asistencia = asistenciaRepository.findById(data.getAsistenciaId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Asistencia not found: " + data.getAsistenciaId()));

                    asistencia.setStatus("PROCESADA");
                    asistencia.setParticipantId(pass.getParticipantId()); // Actualizar participantId
                    asistenciaRepository.save(asistencia);

                    log.info("✅ Asistencia procesada exitosamente: Pass={}, Puntos añadidos={}, Nuevo balance={}",
                            data.getPassId(), data.getPointsAwarded(), nuevoBalance);

                } catch (Exception e) {
                    log.error("❌ Error procesando asistencia: {}", data, e);

                    // Marcar como FALLIDA
                    asistenciaRepository.findById(data.getAsistenciaId()).ifPresent(a -> {
                        a.setStatus("FALLIDA");
                        asistenciaRepository.save(a);
                    });
                }
            }
        };
    }

    // ---------- HELPER: Verificar logros -----------

    private void checkAchievements(Pass pass, Integer oldBalance, Integer newBalance) {
        // Certificado (25 puntos)
        if (oldBalance < pass.getPointsCertificate() &&
                newBalance >= pass.getPointsCertificate()) {
            pass.setCertificateStatus(Pass.CertificateStatus.REACHED);
            log.info("🏆 Certificado alcanzado: Pass ID {}", pass.getPassId());
        }

        // Acceso Especial (30 puntos)
        if (oldBalance < pass.getPointsSpecialAccess() &&
                newBalance >= pass.getPointsSpecialAccess()) {
            pass.setAccessStatus(Pass.AccessStatus.REACHED);
            log.info("🏆 Acceso Especial alcanzado: Pass ID {}", pass.getPassId());
        }
    }
}
