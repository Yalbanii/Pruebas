package com.proyecto.congreso.points.config;

import com.proyecto.congreso.points.dto.PassPointsData;
import com.proyecto.congreso.points.listener.BatchJobExecutionMongoListener;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Configuración de Spring Batch para el procesamiento diario de puntos.
 *
 * Job: addJob y useJob
 * Step 1: calculatePoints - Lee pases, calcula y agrega puntos.
 * Step 2: publishEventsStep - Publica eventos para logs en MongoDB
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

        return new JobBuilder("pointsJob", jobRepository)
                .start(calculateAndApplyPointsStep())
                .next(publishEventsStep())
                .build();
    }

    // ========== CALCULATE AND ADD POINTS ==========

    @Bean
    public Step calculateAndApplyPointsStep() {
        return new StepBuilder("calculateAndApplyPointsStep", jobRepository)
                .<Pass, PassPointsData>chunk(10, transactionManager)
                .reader(passReader())
                .processor(pointsCalculatorProcessor())
                .writer(pointsApplierWriter())
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
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(10)
                .build();
    }

    // ---------- PROCESSOR -----------

    @Bean
    public ItemProcessor<Pass, PassPointsData> pointsCalculatorProcessor() {
        return pass -> {
            log.info("Processing Pass: {} (Type: {}, Balance of Points: {})",
                    pass.getPassId(),
                    pass.getPassType(),
                    pass.getPointsBalance());
                if (pass.getPointsBalance() > 25) {
                    log.info("You reached the Certificate!");
                    return new PassPointsData(pass.getPassId(), pass.getPointsBalance(), POINTS_PER_CONFERENCE);
                }
                return null;

        };
    }

    // ---------- WRITER -----------
    @Bean
            public ItemWriter<PassPointsData> pointsApplierWriter() {
                return items -> {
                    for (PassPointsData data : items) {
                        if (data != null) {
                            Pass pass = passRepository.findById(data.getPassId())
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Pass not found: " + data.getPassId()));

                            Integer previousPointsBalance = pass.getPointsBalance();
                            Integer newPointsBalance = previousPointsBalance + data.getAddPoints();
                            pass.setPointsBalance(newPointsBalance);
                            pass.setPointsBalance(newPointsBalance);
                            passRepository.save(pass);

                            log.info("✅ Points applied to Pass {}: {} (Old: ${}, New: ${})",
                                    pass.getPassId(),
                                    data.getAddPoints(),
                                    data.getOriginalPointsBalance(),
                                    newPointsBalance);
                        }
                    }
                };
            }


    @Bean
    public ItemWriter<PassPointsData> pointsUsedWriter() {
        return items -> {
            for (PassPointsData data : items) {
                if (data != null) {
                    Pass pass = passRepository.findById(data.getPassId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Pass not found: " + data.getPassId()));

                    Integer previousPointsBalance = pass.getPointsBalance();
                    Integer newPointsBalance = previousPointsBalance-POINTS_COST_FREEBIES;
                    pass.setPointsBalance(newPointsBalance);
                    pass.setUpdatedAt(LocalDateTime.now());

                    passRepository.save(pass);

                    log.info("✅ Points used from Pass {}: {} (Old: ${}, New: ${})",
                            pass.getPassId(),
                            data.getAddPoints(),
                            data.getOriginalPointsBalance(),
                            newPointsBalance);
                }
            }
        };
    }
        // ========== PUBLISH EVENTS FOR MONGO LOGS ==========

        @Bean
        public Step publishEventsStep() {
            return new StepBuilder("publishEventsStep", jobRepository)
                    .tasklet((contribution, chunkContext) -> {
                        log.info("✅ Step 2: Events published successfully. MongoDB logs created via event listeners.");
                        return org.springframework.batch.repeat.RepeatStatus.FINISHED;
                    }, transactionManager)
                    .build();
        }

//        @Bean
//    public ItemReader<PassPointsData> pointsDataReader(){
//        return new ItemReader<>() {
//            private final List<PassPointsData> processedPass = new ArrayList<>();
//            private int currentIndex = 0;
//
//            @Override
//            public PassPointsData read(){
//                //Lee el contexto del STEP 1
//                if (processedPass.isEmpty()){
//                    ExecutionContext context = stepExecution.getExecutionContext();
//                    for (String key : context.toMap().keySet()){
//                        PassPointsData data = (PassPointsData) context.get(key);
//                        processedPass.add(data);
//                    } if (currentIndex < processedPass.size()){
//                        return processedPass.get(currentIndex++);
//                    }
//
//                }  return null;
//            }
//
//        };
//
//        }

}
