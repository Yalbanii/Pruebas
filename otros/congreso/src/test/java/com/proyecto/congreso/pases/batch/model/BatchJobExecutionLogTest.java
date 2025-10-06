package com.proyecto.congreso.pases.batch.model;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class BatchJobExecutionLogTest {

    private static final String ID = "LOG-100";
    private static final Long JOB_EXECUTION_ID = 50L;
    private static final String JOB_NAME = "DailyPointsAwardingJob";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_FAILED = "FAILED";
    private static final Integer TOTAL_PROCESSED = 1000;
    private static final Integer ADDED_POINTS = 50;
    private static final Integer USED_POINTS = 10;
    private static final String ERROR_MSG = "Database connection failed.";
    private static final LocalDateTime MOCK_START_TIME = LocalDateTime.of(2025, 10, 5, 10, 0, 0);
    private static final LocalDateTime MOCK_END_TIME = LocalDateTime.of(2025, 10, 5, 10, 1, 30); // 90 seconds later
    private static final Long EXPECTED_DURATION_MS = Duration.between(MOCK_START_TIME, MOCK_END_TIME).toMillis(); // 90000 ms

    // -------------------------------------------------------------------------
    // 1. Tests de Constructores y POJO
    // -------------------------------------------------------------------------

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFieldsSet() {
        // When
        BatchJobExecutionLog log = new BatchJobExecutionLog(
                ID, JOB_EXECUTION_ID, JOB_NAME, STATUS_COMPLETED, MOCK_START_TIME, MOCK_END_TIME, EXPECTED_DURATION_MS,
                TOTAL_PROCESSED, ADDED_POINTS, USED_POINTS, ERROR_MSG
        );

        // Then
        assertNotNull(log);
        assertEquals(ID, log.getId());
        assertEquals(JOB_NAME, log.getJobName());
        assertEquals(EXPECTED_DURATION_MS, log.getDuration());
        assertEquals(TOTAL_PROCESSED, log.getTotalPassProcessed());
        assertEquals(ERROR_MSG, log.getErrorMessage());
    }

    @Test
    void noArgsConstructor_andSetters_shouldSetAndRetrieveValues() {
        // Given
        BatchJobExecutionLog log = new BatchJobExecutionLog();

        // When
        log.setJobName(JOB_NAME);
        log.setPassWithUsedPoints(USED_POINTS);
        log.setStatus(STATUS_STARTED);

        // Then
        assertEquals(JOB_NAME, log.getJobName());
        assertEquals(USED_POINTS, log.getPassWithUsedPoints());
        assertEquals(STATUS_STARTED, log.getStatus());
    }

    // -------------------------------------------------------------------------
    // 2. Test del Método Estático 'started'
    // -------------------------------------------------------------------------

    @Test
    void started_shouldInitializeJobLogWithStartTimeAndStatus() {
        // Given: Mockear LocalDateTime.now() para controlar el startTime
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(MOCK_START_TIME);

            // When
            BatchJobExecutionLog log = BatchJobExecutionLog.started(JOB_EXECUTION_ID, JOB_NAME);

            // Then
            assertNotNull(log);
            assertEquals(JOB_EXECUTION_ID, log.getJobExecutionId());
            assertEquals(JOB_NAME, log.getJobName());
            assertEquals(STATUS_STARTED, log.getStatus(), "El status debe ser STARTED.");
            assertEquals(MOCK_START_TIME, log.getStartTime(), "El startTime debe ser el tiempo mockeado.");
            assertNull(log.getEndTime(), "EndTime debe ser nulo al inicio.");
            assertNull(log.getDuration(), "Duration debe ser nulo al inicio.");
        }
    }

}