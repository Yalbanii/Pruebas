package com.proyecto.congreso.points.assistance.events;

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
public class AssistanceRegisteredEvent {

    /**
     * ID del Pass que recibirá los puntos
     */
    private Long passId;

    /**
     * ID de la conferencia a la que asistió
     */
    private String conferenciaId;

    /**
     * Título de la conferencia (para logs)
     */
    private String tituloConferencia;

    /**
     * Cantidad de puntos a sumar
     */
    private Integer amountPoints;

    /**
     * Timestamp del evento
     */
    private LocalDateTime timestamp;

    /**
     * Constructor simplificado que establece el timestamp automáticamente
     */
    public AssistanceRegisteredEvent(Long passId, String conferenciaId,
                                     String tituloConferencia, Integer amountPoints) {
        this.passId = passId;
        this.conferenciaId = conferenciaId;
        this.tituloConferencia = tituloConferencia;
        this.amountPoints = amountPoints;
        this.timestamp = LocalDateTime.now();
    }

}