package com.proyecto.congreso.batch.config;

import com.proyecto.congreso.asistencia.dto.PassPointsData;
import com.proyecto.congreso.batch.listener.BatchJobExecutionMongoListener;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

/**
 * Configuración de Spring Batch para el procesamiento diario de puntos.
 *
 * Job: addJob y useJob
 * Step 1: calculatePoints - Lee pases, calcula y agrega puntos.
 * Step 2: publishEventsStep - Publica eventos para logs en MongoDB
 Job: pointsJob
 * Step: calculateAndApplyPointsStep - Lee pases activos, calcula y publica eventos
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.enabled", havingValue = "true", matchIfMissing = true)

public class PointsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PassRepository passRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired(required = false)
    private BatchJobExecutionMongoListener batchJobExecutionMongoListener;

    private static final int POINTS_PER_CONFERENCE = 5; //ejemplo
    private static final int POINTS_COST_FREEBIES = 5; //ejemplo

    // ========== JOB DEFINITION ==========

    @Bean
    public Job pointsJob() {
        JobBuilder jobBuilder = new JobBuilder("pointsJob", jobRepository);

        // Only add MongoDB listener if available
        if (batchJobExecutionMongoListener != null) {
            jobBuilder.listener(batchJobExecutionMongoListener);
        }

        return jobBuilder
                .start(calculateAndApplyPointsStep())
                .build();
    }

    // ========== CALCULATE AND ADD POINTS ==========

    @Bean
    public Step calculateAndApplyPointsStep() {
        return new StepBuilder("calculateAndApplyPointsStep", jobRepository)
                .<Pass, PassPointsData>chunk(10, transactionManager)
                .reader(passReader())
                .processor(pointsCalculatorProcessor())
                .writer(pointsEventPublisherWriter())
                .build();
    }

    // ---------- READER -----------
    //Solo va a leer las cuentas activas,y las va a procesar en chunks de 10
    @Bean
    public RepositoryItemReader<Pass> passReader() {
        return new RepositoryItemReaderBuilder<Pass>()
                .name("passReader")
                .repository(passRepository)
                .methodName("findActivePass")
                .sorts(Collections.singletonMap("passId", Sort.Direction.ASC))
                .pageSize(10)
                .build();
    }

    // ---------- PROCESSOR -----------

    @Bean
    public ItemProcessor<Pass, PassPointsData> pointsCalculatorProcessor() {
        return pass -> {
            log.info("📊 Processing Pass: {} (Balance: {})",
                    pass.getPassId(),
                    pass.getPointsBalance());

            // Ejemplo: Procesar solo pases con menos de 50 puntos
            if (pass.getPointsBalance() < 50) {
                return new PassPointsData(
                        pass.getPassId(),
                        pass.getPointsBalance(),
                        POINTS_PER_CONFERENCE
                );
            }

            return null; // Skip este Pass
        };
    }


    // ---------- WRITER -----------

    @Bean
    public ItemWriter<PassPointsData> pointsEventPublisherWriter() {
        return items -> {
            for (PassPointsData data : items) {
                if (data != null) {
                    // Publicar evento para que AssistancePointsHandler lo procese
                    AssistancePointsEvent event = AssistancePointsEvent.builder()
                            .passId(data.getPassId())
                            .amountPoints(data.getAddPoints())
                            .movementType("BATCH_ADD")
                            .build();

                    eventPublisher.publishEvent(event);
                    log.info("✅ Evento publicado para Pass ID: {} (+{} puntos)",
                            data.getPassId(), data.getAddPoints());
                }
            }
        };
    }
}
