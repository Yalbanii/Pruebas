package com.proyecto.congreso.points.assistance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaPointsData implements Serializable {

    static final long serialVersionUID = 1L;

    //ID de la asistencia en MongoDB
    private String asistenciaId;

     // ID del Pass en MySQL (para actualizar balance)
    private Long passId;

    private String conferenciaId;

    private Integer pointsAwarded;

    // Balance actual del Pass ANTES y DESPUES de sumar puntos
    private Integer currentBalance;

    private Integer newBalance;


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
