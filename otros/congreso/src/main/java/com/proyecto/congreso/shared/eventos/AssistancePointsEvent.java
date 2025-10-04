package com.proyecto.congreso.shared.eventos;

import lombok.*;

import java.time.LocalDateTime;

/*
 * Evento que representa la acumulación de puntos por asistencia a conferencias.
 * Este evento se publica cuando un participante asiste a una conferencia
 * y debe recibir puntos en su Pass.
 */
//Acumulacion de puntos
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssistancePointsEvent{
    // Tipo de movimiento: ADD, USE, etc.
    private String movementType;

    // ID o título de la conferencia origen
    private String sourceConferenceId;

    // ID del participante objetivo
    private Long targetParticipantId;

    // ID del Pass que recibirá los puntos
    private Long passId;

    // Cantidad de puntos a agregar
    private Integer amountPoints;

    // Balance de puntos después de la operación (calculado por el listener)
    private Integer pointsAfter;

    // Timestamp del evento
    private LocalDateTime timestamp;

    public AssistancePointsEvent(Long passId, Integer addPoints) {
    }


    /*
     * Constructor simplificado para el caso más común:
     * Solo se necesita el passId y los puntos a agregar.
     * El resto se calcula en el handler.
     */
    public static AssistancePointsEvent forAttendance(Long passId, Long participantId,
                                                      Long conferenceId, String conferenciaTitle,
                                                      Integer points) {
        return AssistancePointsEvent.builder()
                .passId(passId)
                .targetParticipantId(participantId)
                .sourceConferenceId(conferenceId.toString())
                .amountPoints(points)
                .movementType("ATTENDANCE")
                .timestamp(LocalDateTime.now())
                .build();
    }
}