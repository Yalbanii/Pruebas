package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.Certificate;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CertificateResponseTest {

    @Test
    void shouldMapCertificateEntityToCertificateResponseSuccessfully() {
        // Given
        Long certificateId = 5L;
        Long passId = 10L;
        Long participantId = 100L;
        String email = "test@example.com";
        String name = "Alice Smith";
        Integer points = 50;
        Boolean reached = true;
        String code = "CERT12345";
        LocalDateTime now = LocalDateTime.now();

        // 1. Crear una instancia de la entidad (Certificate)
        Certificate certificateEntity = new Certificate();
        certificateEntity.setCertificateId(certificateId);
        certificateEntity.setPassId(passId);
        certificateEntity.setParticipantId(participantId);
        certificateEntity.setParticipantEmail(email);
        certificateEntity.setParticipantName(name);
        certificateEntity.setPointsAchieved(points);
        certificateEntity.setReached(reached);
        certificateEntity.setCertificateCode(code);
        certificateEntity.setCreatedAt(now);

        // When
        // 2. Llamar al método estático que queremos probar
        CertificateResponse response = CertificateResponse.fromEntity(certificateEntity);

        // Then
        // 3. Verificar que todos los campos de la respuesta (DTO) sean iguales a los campos de la entidad
        assertNotNull(response);
        assertEquals(certificateId, response.getCertificateId(), "Certificate ID should match.");
        assertEquals(passId, response.getPassId(), "Pass ID should match.");
        assertEquals(participantId, response.getParticipantId(), "Participant ID should match.");
        assertEquals(email, response.getParticipantEmail(), "Email should match.");
        assertEquals(name, response.getParticipantName(), "Name should match.");
        assertEquals(points, response.getPointsAchieved(), "Points achieved should match.");
        assertTrue(response.getReached(), "Reached status should be true.");
        assertEquals(code, response.getCertificateCode(), "Certificate code should match.");
        assertEquals(now, response.getCreatedAt(), "Creation date should match.");
    }

    @Test
    void shouldHandleNullFieldsInCertificateEntity() {
        // Given
        // Probar el mapeo cuando la entidad tiene campos nulos
        Certificate certificateEntity = new Certificate();
        certificateEntity.setCertificateId(1L);

        // 💡 SOLUCIÓN: Forzar el valor a null en la entidad
        // Esto es necesario porque tu aserción espera null en la respuesta.
        certificateEntity.setReached(null);

        // Cuando
        CertificateResponse response = CertificateResponse.fromEntity(certificateEntity);

        // Entonces
        assertNotNull(response);
        assertEquals(1L, response.getCertificateId());

        // Verificamos que los campos nulos se mapeen como nulos o los valores por defecto del DTO
        assertNull(response.getParticipantEmail(), "Email should be null.");
        assertNull(response.getPointsAchieved(), "Points achieved should be null.");
        // Esta aserción ahora pasará porque el campo fue seteado explícitamente a null en la entidad.
        assertNull(response.getReached(), "Reached status should be null (Boolean wrapper).");
    }
}
