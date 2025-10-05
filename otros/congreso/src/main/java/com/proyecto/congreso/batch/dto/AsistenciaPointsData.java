package com.proyecto.congreso.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para transportar datos entre los steps del Batch Job.
 *
 * Contiene la información de una asistencia que se va a procesar:
 * - Identifica la asistencia en MongoDB
 * - Identifica el Pass en MySQL
 * - Contiene los puntos a sumar
 * - Almacena el balance actual y nuevo para auditoría
 *
 * Este DTO es procesado por:
 * 1. Reader: No lo crea (lee Asistencia directamente)
 * 2. Processor: LO CREA a partir de Asistencia + Pass
 * 3. Writer: LO CONSUME para actualizar MySQL y MongoDB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaPointsData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID de la asistencia en MongoDB (para marcarla como procesada)
     */
    private String asistenciaId;

    /**
     * ID del Pass en MySQL (para actualizar balance)
     */
    private Long passId;

    /**
     * ID de la conferencia a la que asistió
     */
    private String conferenciaId;

    /**
     * Puntos que otorga la conferencia (ya vienen de la asistencia)
     */
    private Integer pointsAwarded;

    /**
     * Balance actual del Pass ANTES de sumar puntos
     */
    private Integer currentBalance;

    /**
     * Balance calculado DESPUÉS de sumar puntos
     */
    private Integer newBalance;

    /**
     * Constructor de conveniencia para el Processor
     */
    public AsistenciaPointsData(String asistenciaId, Long passId, String conferenciaId,
                                Integer pointsAwarded, Integer currentBalance) {
        this.asistenciaId = asistenciaId;
        this.passId = passId;
        this.conferenciaId = conferenciaId;
        this.pointsAwarded = pointsAwarded;
        this.currentBalance = currentBalance;
        this.newBalance = currentBalance + pointsAwarded;
    }
}
