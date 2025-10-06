package com.proyecto.congreso.points.assistance.dto;

import com.proyecto.congreso.points.assistance.model.Asistencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistanceResponse {
    private String id;
    private Long passId;
    private Long participantId;
    private String conferenciaId;
    private String tituloConferencia;
    private Integer puntosOtorgados;
    private LocalDateTime fechaAsistencia;
    private String status;


    public static AssistanceResponse fromEntity(Asistencia asistencia) {
        return new AssistanceResponse(
                asistencia.getId(),
                asistencia.getPassId(),
                asistencia.getParticipantId(),
                asistencia.getConferenciaId(),
                asistencia.getTituloConferencia(),
                asistencia.getPuntosOtorgados(),
                asistencia.getFechaAsistencia(),
                asistencia.getStatus()
        );
    }


}
