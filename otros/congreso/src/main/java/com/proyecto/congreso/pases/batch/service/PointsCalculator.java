package com.proyecto.congreso.pases.batch.service;

import com.proyecto.congreso.pases.model.Pass;

/**
 * Interface que define el contrato para el cálculo de puntos.
 * Implementa el patrón Strategy para permitir diferentes cálculos
 * según el tipo de movimiento (POLIMORFISMO).
 */
public interface PointsCalculator {

   Integer addPoints(Pass pass, Integer pointsToAdd);

   Integer usePoints(Pass pass, Integer pointsToUse);

   Integer getPuntos();

   Pass.PointsMovementAdd getPointsMovementAdd();

   Pass.PointsMovementUse getPointsMovementUse();

   Pass.AccessStatus getAccessStatus();

   Pass.CertificateStatus getCertificateStatus();

}
