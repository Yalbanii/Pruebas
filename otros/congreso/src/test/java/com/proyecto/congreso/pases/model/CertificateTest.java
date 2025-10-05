package com.proyecto.congreso.pases.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
public class CertificateTest {

    private Long PASS_ID = 10L;
    private Long PARTICIPANT_ID = 200L;
    private String PARTICIPANT_EMAIL = "john.doe@example.com";
    private String PARTICIPANT_NAME = "John Doe";
    private Integer POINTS_ACHIEVED = 95;

    // -------------------------------------------------------------------------
    // Prueba de Funcionalidad Básica (Constructores y Getters/Setters)
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateEmptyCertificateInstance() {
        // Prueba el constructor @NoArgsConstructor
        Certificate certificate = new Certificate();
        assertNotNull(certificate);

        // Verifica el valor por defecto de 'reached'
        assertTrue(certificate.getReached(), "Reached should default to true.");
    }

    @Test
    void shouldCreateFullCertificateInstance() {
        // Prueba el constructor @AllArgsConstructor
        LocalDateTime now = LocalDateTime.now();
        Certificate certificate = new Certificate(
                1L, PASS_ID, PARTICIPANT_ID, PARTICIPANT_EMAIL, PARTICIPANT_NAME,
                POINTS_ACHIEVED, false, now, "CODE123"
        );

        assertNotNull(certificate);
        assertEquals(1L, certificate.getCertificateId());
        assertEquals(POINTS_ACHIEVED, certificate.getPointsAchieved());
        assertFalse(certificate.getReached(), "Should allow setting reached to false via constructor.");
    }

    // -------------------------------------------------------------------------
    // Prueba del Método de Fábrica (Factory Method)
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateCertificateUsingFactoryMethodSuccessfully() {
        // When
        Certificate certificate = Certificate.create(
                PASS_ID,
                PARTICIPANT_ID,
                PARTICIPANT_EMAIL,
                PARTICIPANT_NAME,
                POINTS_ACHIEVED
        );

        // Then
        assertNotNull(certificate);

        // Verificación de campos seteados manualmente
        assertEquals(PASS_ID, certificate.getPassId(), "Pass ID should be set.");
        assertEquals(PARTICIPANT_ID, certificate.getParticipantId(), "Participant ID should be set.");
        assertEquals(PARTICIPANT_EMAIL, certificate.getParticipantEmail(), "Email should be set.");
        assertEquals(POINTS_ACHIEVED, certificate.getPointsAchieved(), "Points achieved should be set.");

        // Verificación de valor forzado/por defecto
        assertTrue(certificate.getReached(), "Reached should be explicitly set to true by the factory.");

        // Verificación de campo generado
        assertNotNull(certificate.getCertificateCode(), "Certificate code must be generated.");
        assertTrue(certificate.getCertificateCode().startsWith("CERT-"), "Code must start with CERT-.");
    }

    // -------------------------------------------------------------------------
    // Prueba del Código Generado (generateCertificateCode)
    // -------------------------------------------------------------------------

    @Test
    void shouldGenerateCertificateCodeInCorrectFormat() {
        // Dado que generateCertificateCode es privado, probamos el resultado
        // a través del método público que lo llama (create).

        // When
        Certificate certificate = Certificate.create(PASS_ID, PARTICIPANT_ID, null, null, 0);
        String code = certificate.getCertificateCode();

        // Then
        // El formato esperado es: CERT-{PASS_ID}-{PARTICIPANT_ID}-{TIMESTAMP}
        // Ejemplo: CERT-10-200-1678886400000

        // Usamos una expresión regular para validar el formato dinámico
        String expectedPattern = String.format("CERT-%d-%d-\\d+", PASS_ID, PARTICIPANT_ID);
        Pattern pattern = Pattern.compile(expectedPattern);

        assertTrue(pattern.matcher(code).matches(),
                "Generated code does not match the required format CERT-{PASS_ID}-{PARTICIPANT_ID}-{TIMESTAMP}.");
    }

    @Test
    void shouldGenerateUniqueCertificateCodesOverTime() throws InterruptedException {
        // Given
        Certificate cert1 = Certificate.create(PASS_ID, PARTICIPANT_ID, null, null, 0);

        // Esperamos un breve momento para asegurar un timestamp diferente
        Thread.sleep(10);

        Certificate cert2 = Certificate.create(PASS_ID, PARTICIPANT_ID, null, null, 0);

        // Then
        assertNotEquals(cert1.getCertificateCode(), cert2.getCertificateCode(),
                "Two codes generated sequentially must be different due to timestamp.");
    }
}
