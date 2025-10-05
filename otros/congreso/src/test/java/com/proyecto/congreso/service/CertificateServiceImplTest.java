package com.proyecto.congreso.service;

import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.repository.CertificateRepository;
import com.proyecto.congreso.pases.service.CertificateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CertificateServiceImplTest {
    @Mock // Simula el repositorio (la dependencia)
    private CertificateRepository certificateRepository;

    @InjectMocks // Inyecta los mocks en la clase a probar
    private CertificateServiceImpl certificateService;

    private Certificate testCertificate1;
    private Long PASS_ID = 10L;

    @BeforeEach
    void setUp() {
        testCertificate1 = new Certificate();
        testCertificate1.setCertificateId(1L);
        testCertificate1.setPassId(PASS_ID);
    }

    // -------------------------------------------------------------------------
    // Test: getAllCertificate()
    // -------------------------------------------------------------------------

    @Test
    void shouldGetAllCertificatesSuccessfully() {
        // Given
        Certificate testCertificate2 = new Certificate();
        testCertificate2.setCertificateId(2L);
        List<Certificate> expectedCertificates = Arrays.asList(testCertificate1, testCertificate2);

        // Cuando el repositorio llama a findAll(), devuelve nuestra lista simulada
        when(certificateRepository.findAll()).thenReturn(expectedCertificates);

        // When
        List<Certificate> actualCertificates = certificateService.getAllCertificate();

        // Then
        assertNotNull(actualCertificates, "The returned list should not be null.");
        assertEquals(2, actualCertificates.size(), "The list size should match the mock data.");

        // Verificamos que el método del repositorio fue llamado
        verify(certificateRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCertificatesExist() {
        // Given
        // Cuando el repositorio llama a findAll(), devuelve una lista vacía
        when(certificateRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Certificate> actualCertificates = certificateService.getAllCertificate();

        // Then
        assertNotNull(actualCertificates, "The returned list should not be null.");
        assertTrue(actualCertificates.isEmpty(), "The list should be empty.");

        verify(certificateRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // Test: existsByPassId(Long passId)
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnTrueWhenCertificateExistsForGivenPassId() {
        // Given
        // Cuando el repositorio llama a existsByPassId con el ID correcto, devuelve true
        when(certificateRepository.existsByPassId(PASS_ID)).thenReturn(true);

        // When
        boolean exists = certificateService.existsByPassId(PASS_ID);

        // Then
        assertTrue(exists, "Should return true because a certificate with the Pass ID exists.");

        // Verificamos que el método del repositorio fue llamado con el ID correcto
        verify(certificateRepository, times(1)).existsByPassId(PASS_ID);
    }

    @Test
    void shouldReturnFalseWhenCertificateDoesNotExistForGivenPassId() {
        // Given
        Long NON_EXISTENT_PASS_ID = 99L;
        // Cuando el repositorio llama a existsByPassId con el ID incorrecto, devuelve false
        when(certificateRepository.existsByPassId(NON_EXISTENT_PASS_ID)).thenReturn(false);

        // When
        boolean exists = certificateService.existsByPassId(NON_EXISTENT_PASS_ID);

        // Then
        assertFalse(exists, "Should return false because no certificate with the Pass ID exists.");

        verify(certificateRepository, times(1)).existsByPassId(NON_EXISTENT_PASS_ID);
    }
}
