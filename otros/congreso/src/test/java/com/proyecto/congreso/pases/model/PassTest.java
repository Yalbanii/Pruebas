package com.proyecto.congreso.pases.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PassTest {
    @Autowired
    private Validator validator;

    private final Long VALID_PARTICIPANT_ID = 50L;

    // -------------------------------------------------------------------------
    // Prueba de Funcionalidad Básica y Valores por Defecto
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateEmptyPassInstanceWithDefaultValues() {
        // Prueba el constructor @NoArgsConstructor
        Pass pass = new Pass();
        assertNotNull(pass);

        // 1. Verificación de valores por defecto (establecidos en la declaración del campo)
        assertEquals(0, pass.getPointsBalance(), "Points Balance should default to 0.");
        assertEquals(25, pass.getPointsCertificate(), "Points Certificate should default to 25.");
        assertEquals(30, pass.getPointsSpecialAccess(), "Points Special Access should default to 30.");
        assertEquals(Pass.PassStatus.ACTIVE, pass.getStatus(), "Status should default to ACTIVE.");
        assertEquals(Pass.AccessStatus.NOT_REACHED, pass.getAccessStatus(), "Access Status should default to NOT_REACHED.");
        assertEquals(Pass.CertificateStatus.NOT_REACHED, pass.getCertificateStatus(), "Certificate Status should default to NOT_REACHED.");

        // 2. Verificación de campos sin valor por defecto (deben ser null)
        assertNull(pass.getPassId(), "ID should be null.");
        assertNull(pass.getPassType(), "Pass Type should be null.");
        assertNull(pass.getParticipantId(), "Participant ID should be null.");
        assertNull(pass.getCreatedAt(), "CreatedAt should be null before persistence.");
    }

    @Test
    void shouldCreateFullPassInstanceUsingAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        // Prueba el constructor @AllArgsConstructor (requiere todos los campos)
        Pass pass = new Pass(
                1L,
                Pass.PassType.ALL_INCLUDED,
                100,
                50,
                60,
                Pass.PointsMovementAdd.ADD,
                Pass.PointsMovementUse.USE,
                VALID_PARTICIPANT_ID,
                Pass.PassStatus.CLOSED,
                Pass.AccessStatus.REACHED,
                Pass.CertificateStatus.REACHED,
                now,
                now.plusDays(1)
        );

        // Then
        assertNotNull(pass);
        assertEquals(1L, pass.getPassId());
        assertEquals(Pass.PassType.ALL_INCLUDED, pass.getPassType());
        assertEquals(100, pass.getPointsBalance());
        assertEquals(Pass.PassStatus.CLOSED, pass.getStatus());
        assertEquals(Pass.AccessStatus.REACHED, pass.getAccessStatus());
        assertEquals(Pass.CertificateStatus.REACHED, pass.getCertificateStatus());
        assertEquals(Pass.PointsMovementAdd.ADD, pass.getPointsAdd());
        assertEquals(Pass.PointsMovementUse.USE, pass.getPointsUse());
    }

    // -------------------------------------------------------------------------
    // Pruebas de Validación (Restricción @NotNull)
    // -------------------------------------------------------------------------

    @Test
    void shouldPassValidationWhenRequiredFieldsArePresent() {
        // Given
        Pass validPass = new Pass();
        validPass.setParticipantId(VALID_PARTICIPANT_ID);
        validPass.setPassType(Pass.PassType.GENERAL);
        // Los demás campos @Column(nullable = false) tienen valores por defecto

        // When
        Set<ConstraintViolation<Pass>> violations = validator.validate(validPass);

        // Then
        assertTrue(violations.isEmpty(), "A pass with required fields set should pass validation.");
    }

    @Test
    void shouldFailValidationWhenParticipantIdIsNull() {
        // Given
        Pass invalidPass = new Pass();
        // ParticipantId es null
        invalidPass.setPassType(Pass.PassType.GENERAL);

        // When
        Set<ConstraintViolation<Pass>> violations = validator.validate(invalidPass);

        // Then
        assertFalse(violations.isEmpty(), "Validation must fail when Participant ID is null.");

        // Verificamos que el error sea específicamente sobre Participant ID
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("participantId") &&
                        v.getMessage().equals("Participant ID is required")
        ));
    }

}
