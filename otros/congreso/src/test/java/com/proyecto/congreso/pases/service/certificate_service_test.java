package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificateServiceImpl - Unit Tests")
class CertificateServiceImplTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    private Certificate testCertificate;

    @BeforeEach
    void setUp() {
        testCertificate = new Certificate();
        testCertificate.setCertificateId(1L);
        testCertificate.setPassId(100L);
        testCertificate.setParticipantId(50L);
        testCertificate.setParticipantEmail("test@example.com");
        testCertificate.setParticipantName("Juan Perez");
        testCertificate.setPointsAchieved(25);
        testCertificate.setReached(true);
        testCertificate.setCertificateCode("CERT-100-50-123456");
        testCertificate.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get all certificates successfully")
    void getAllCertificate_Success() {
        // Arrange
        Certificate cert2 = new Certificate();
        cert2.setCertificateId(2L);
        List<Certificate> certificates = Arrays.asList(testCertificate, cert2);
        when(certificateRepository.findAll()).thenReturn(certificates);

        // Act
        List<Certificate> result = certificateService.getAllCertificate();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(certificateRepository).findAll();
    }


    @Test
    @DisplayName("Should check if certificate exists by pass id")
    void existsByPassId_ReturnsTrue() {
        // Arrange
        when(certificateRepository.existsByPassId(100L)).thenReturn(true);

        // Act
        boolean result = certificateService.existsByPassId(100L);

        // Assert
        assertTrue(result);
        verify(certificateRepository).existsByPassId(100L);
    }

    @Test
    @DisplayName("Should return false when certificate does not exist by pass id")
    void existsByPassId_ReturnsFalse() {
        // Arrange
        when(certificateRepository.existsByPassId(999L)).thenReturn(false);

        // Act
        boolean result = certificateService.existsByPassId(999L);

        // Assert
        assertFalse(result);
        verify(certificateRepository).existsByPassId(999L);
    }

    @Test
    @DisplayName("Should return empty list when no certificates exist")
    void getAllCertificate_EmptyList() {
        // Arrange
        when(certificateRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Certificate> result = certificateService.getAllCertificate();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(certificateRepository).findAll();
    }
}