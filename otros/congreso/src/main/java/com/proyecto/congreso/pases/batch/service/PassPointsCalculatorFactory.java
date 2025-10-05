package com.proyecto.congreso.pases.batch.service;

import com.proyecto.congreso.pases.model.Pass;
import org.springframework.stereotype.Component;

@Component
public class PassPointsCalculatorFactory implements PointsCalculator {
    // Lógica de Agregar Puntos (Implementación de la interfaz)
    @Override
    public Integer addPoints(Pass pass, Integer pointsAdd) {
        // Se pueden agregar mas elementos a la logica (logs, validaciones, etc.)
        return pass.getPointsBalance() + pointsAdd;
    }

    // Lógica de Usar Puntos (Implementación de la interfaz)
    @Override
    public Integer usePoints(Pass pass, Integer pointsUse) {
        if (pass.getPointsBalance() < pointsUse) {
            throw new IllegalArgumentException("❌ Not enough points in balance.");
        }
        return pass.getPointsBalance() - pointsUse;
    }

    @Override
    public Integer getPuntos() { return 1; } // Valor genérico, se obtiene del evento
    @Override
    public Pass.PointsMovementAdd getPointsMovementAdd() { return Pass.PointsMovementAdd.ADD; }
    @Override
    public Pass.PointsMovementUse getPointsMovementUse() { return Pass.PointsMovementUse.USE; }
    @Override
    public Pass.AccessStatus getAccessStatus() { return Pass.AccessStatus.NOT_REACHED; }
    @Override
    public Pass.CertificateStatus getCertificateStatus() { return Pass.CertificateStatus.NOT_REACHED; }
}