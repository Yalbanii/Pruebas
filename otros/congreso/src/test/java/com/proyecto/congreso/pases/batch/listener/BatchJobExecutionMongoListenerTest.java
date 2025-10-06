package com.proyecto.congreso.pases.batch.listener;

import com.proyecto.congreso.pases.batch.model.BatchJobExecutionLog;
import com.proyecto.congreso.pases.batch.repository.BatchJobExecutionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.item.ExecutionContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Importante: Usamos MockitoExtension para inicializar los mocks
@ExtendWith(MockitoExtension.class)
class BatchJobExecutionMongoListenerTest {

    private static final Long JOB_EXECUTION_ID = 123L;
    private static final String JOB_NAME = "PassPointsUpdateJob";
    private static final String LOG_ID = "MONGO-LOG-ID";

    @Mock
    private BatchJobExecutionLogRepository logRepository;

    @InjectMocks
    private BatchJobExecutionMongoListener listener;

    // Mocks de Spring Batch
    @Mock
    private JobExecution jobExecution;
    @Mock
    private JobInstance jobInstance;
    @Mock
    private ExecutionContext executionContext;

    private BatchJobExecutionLog mockStartedLog;

    @BeforeEach
    void setUp() {
        // Configuración común de JobExecution
        lenient().when(jobExecution.getId()).thenReturn(JOB_EXECUTION_ID);
        lenient().when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        lenient().when(jobInstance.getJobName()).thenReturn(JOB_NAME);
        lenient().when(jobExecution.getExecutionContext()).thenReturn(executionContext);

        // Simular un log que ya fue creado (para usar en afterJob)
        mockStartedLog = new BatchJobExecutionLog();
        mockStartedLog.setId(LOG_ID);
        mockStartedLog.setJobExecutionId(JOB_EXECUTION_ID);
        mockStartedLog.setStatus("STARTED");
    }

    // -------------------------------------------------------------------------
    // 1. Test: beforeJob
    // -------------------------------------------------------------------------

    @Test
    void beforeJob_shouldCreateStartedLogAndSave() {
        // When
        listener.beforeJob(jobExecution);

        // Then
        // 1. Capturar el log que se intenta guardar
        ArgumentCaptor<BatchJobExecutionLog> logCaptor = ArgumentCaptor.forClass(BatchJobExecutionLog.class);
        verify(logRepository).save(logCaptor.capture());

        BatchJobExecutionLog savedLog = logCaptor.getValue();

        // 2. Verificar que el método estático 'started' fue llamado y que los campos están correctos
        assertEquals(JOB_EXECUTION_ID, savedLog.getJobExecutionId());
        assertEquals(JOB_NAME, savedLog.getJobName());
        assertEquals("STARTED", savedLog.getStatus());
        assertNotNull(savedLog.getStartTime());
    }


    @Test
    void afterJob_shouldHandleNoLogFoundGracefully() {
        // Given
        when(jobExecution.getId()).thenReturn(999L);
        when(logRepository.findByJobExecutionId(999L)).thenReturn(null); // Log no encontrado

        // When
        listener.afterJob(jobExecution);

        // Then
        // Verificar que no se llamó a save ni a ningún método en el logRepository después del find
        verify(logRepository, never()).save(any());
        // El método simplemente debe terminar después de emitir la advertencia (warn)
    }
}
