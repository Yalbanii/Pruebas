package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.Pass;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Necesario para inyectar el Validator de Spring
public class PassRequestTest {

    @Autowired
    private Validator validator;

    // Constantes para un objeto válido
    private static final Long VALID_PARTICIPANT_ID = 10L;
    private static final Pass.PassType VALID_PASS_TYPE = Pass.PassType.GENERAL;

    // -------------------------------------------------------------------------
    // Pruebas de Validación (Restricciones @NotNull)
    // -------------------------------------------------------------------------

    @Test
    void shouldPassValidationWhenRequestIsValid() {
        // Given
        PassRequest validRequest = new PassRequest(VALID_PARTICIPANT_ID, VALID_PASS_TYPE, 50);

        // When
        Set<ConstraintViolation<PassRequest>> violations = validator.validate(validRequest);

        // Then
        assertTrue(violations.isEmpty(), "A valid request should have no constraint violations.");
    }

    @Test
    void shouldFailValidationWhenParticipantIdIsNull() {
        // Given
        // participantId es null (lo que queremos probar)
        PassRequest invalidRequest = new PassRequest(null, VALID_PASS_TYPE, 0);

        // When
        Set<ConstraintViolation<PassRequest>> violations = validator.validate(invalidRequest);

        // Then
        assertFalse(violations.isEmpty(), "Validation must fail when Participant ID is null.");
        assertEquals(1, violations.size(), "There should be exactly one violation.");

        // Verificamos que el mensaje y el campo de la violación sean correctos
        ConstraintViolation<PassRequest> violation = violations.iterator().next();
        assertEquals("Participant ID is required", violation.getMessage());
        assertEquals("participantId", violation.getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenPassTypeIsNull() {
        // Given
        // passType es null (lo que queremos probar)
        PassRequest invalidRequest = new PassRequest(VALID_PARTICIPANT_ID, null, 0);

        // When
        Set<ConstraintViolation<PassRequest>> violations = validator.validate(invalidRequest);

        // Then
        assertFalse(violations.isEmpty(), "Validation must fail when Pass Type is null.");
        assertEquals(1, violations.size(), "There should be exactly one violation.");

        // Verificamos que el mensaje y el campo de la violación sean correctos
        ConstraintViolation<PassRequest> violation = violations.iterator().next();
        assertEquals("Pass type is required", violation.getMessage());
        assertEquals("passType", violation.getPropertyPath().toString());
    }

    @Test
    void shouldPassValidationWhenPointsBalanceIsNull() {
        // Given
        // pointsBalance es null, pero NO tiene restricción @NotNull
        PassRequest validRequest = new PassRequest(VALID_PARTICIPANT_ID, VALID_PASS_TYPE, null);

        // When
        Set<ConstraintViolation<PassRequest>> violations = validator.validate(validRequest);

        // Then
        // La validación DEBE pasar, ya que 'pointsBalance' no tiene @NotNull
        assertTrue(violations.isEmpty(), "Validation should pass even if Points Balance is null.");
    }
}

