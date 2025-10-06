package com.proyecto.congreso.points.assistance.events;

import lombok.*;

import java.time.LocalDateTime;

//Acumulacion de puntos
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssistanceRegisteredEvent {

    private Long passId;

    private String conferenciaId;

    private String tituloConferencia;

    private Integer amountPoints;

    private LocalDateTime timestamp;

    public AssistanceRegisteredEvent(Long passId, String conferenciaId,
                                     String tituloConferencia, Integer amountPoints) {
        this.passId = passId;
        this.conferenciaId = conferenciaId;
        this.tituloConferencia = tituloConferencia;
        this.amountPoints = amountPoints;
        this.timestamp = LocalDateTime.now();
    }

    public AssistanceRegisteredEvent(Long passId, Integer pointsToAdd) {
    }
}