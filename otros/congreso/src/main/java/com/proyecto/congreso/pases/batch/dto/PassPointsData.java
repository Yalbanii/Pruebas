package com.proyecto.congreso.pases.batch.dto;

import com.proyecto.congreso.pases.model.Pass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO que contiene la información de un Tipo de Pase.
 * Se usa para pasar datos entre los steps del batch job.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassPointsData {

    private Long passId;
    private Pass.PassType passType;
    private Integer originalPointsBalance;
    private Integer addPoints;
    private Integer usePoints;
    private Integer newPointsBalance;
    private Long participantId;
    private String participantEmail;
    private LocalDateTime calculatedAt;

    public PassPointsData(Pass pass, Integer points) {
        this.passId = pass.getPassId();
        this.passType = pass.getPassType();
        this.participantId = pass.getParticipantId();
        this.originalPointsBalance = pass.getPointsBalance();
        this.addPoints = points;
        this.newPointsBalance = originalPointsBalance + points;
        this.usePoints = points;
        this.newPointsBalance = originalPointsBalance - points;
        this.calculatedAt = LocalDateTime.now();
    }

    public PassPointsData(@NotNull(message = "Pass ID is required") Long passId, Integer pointsBalance, int pointsPerConference) {
    }
}
