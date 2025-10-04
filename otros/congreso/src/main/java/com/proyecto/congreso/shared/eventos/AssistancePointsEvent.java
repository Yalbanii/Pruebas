package com.proyecto.congreso.shared.eventos;

import lombok.*;

import java.time.LocalDateTime;

//Acumulacion de puntos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistancePointsEvent{
    private String movementType;
    private String sourceConferenceId;
    private Long targetParticipantId;
    private Long passId;
    private Integer amountPoints;
    private Integer pointsAfter;
    private LocalDateTime timestamp;

    public AssistancePointsEvent(Long passId, Integer addPoints) {
    }
}