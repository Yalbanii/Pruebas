package com.proyecto.congreso.pases.batch.service;

import com.proyecto.congreso.pases.model.Pass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PassPointsCalculatorFactoryTest {
    private PassPointsCalculatorFactory calculator;
    private Pass passWith100Points;
    private Pass passWith10Points;

    @BeforeEach
    void setUp() {
        calculator = new PassPointsCalculatorFactory();
        passWith100Points = new Pass();
        passWith10Points = new Pass();
    }


    @Test
    void usePoints_shouldThrowExceptionWhenBalanceIsInsufficient() {
        // Given
        Integer pointsToUse = 50; // Solo tiene 10

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.usePoints(passWith10Points, pointsToUse);
        }, "Debe lanzar IllegalArgumentException si no hay suficientes puntos.");

        // Opcional: Verificar el mensaje de error exacto
        try {
            calculator.usePoints(passWith10Points, pointsToUse);
            fail("Se esperaba que lanzara una excepción.");
        } catch (IllegalArgumentException e) {
            assertEquals("❌ Not enough points in balance.", e.getMessage());
        }
    }

    @Test
    void getPuntos_shouldReturnGenericValue() {
        assertEquals(1, calculator.getPuntos(), "getPuntos debe devolver 1 como valor genérico.");
    }

    @Test
    void getPointsMovementAdd_shouldReturnAddEnum() {
        assertEquals(Pass.PointsMovementAdd.ADD, calculator.getPointsMovementAdd());
    }

    @Test
    void getPointsMovementUse_shouldReturnUseEnum() {
        assertEquals(Pass.PointsMovementUse.USE, calculator.getPointsMovementUse());
    }

    @Test
    void getAccessStatus_shouldReturnNotReachedEnum() {
        assertEquals(Pass.AccessStatus.NOT_REACHED, calculator.getAccessStatus());
    }

    @Test
    void getCertificateStatus_shouldReturnNotReachedEnum() {
        assertEquals(Pass.CertificateStatus.NOT_REACHED, calculator.getCertificateStatus());
    }
}
