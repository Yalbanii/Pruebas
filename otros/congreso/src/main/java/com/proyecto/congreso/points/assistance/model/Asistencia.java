package com.proyecto.congreso.points.assistance.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Long passId;
    private Long participantId;
    private String conferenciaId;
    private String tituloConferencia;
    private Integer puntosOtorgados;
    private LocalDateTime fechaAsistencia;
    private String status;

    public static Asistencia crear(Long passId, Long participantId, String conferenciaId,
                                   String tituloConferencia, Integer puntos) {
        Asistencia asistencia = new Asistencia();
        asistencia.setPassId(passId);
        asistencia.setParticipantId(participantId);
        asistencia.setConferenciaId(conferenciaId);
        asistencia.setTituloConferencia(tituloConferencia);
        asistencia.setPuntosOtorgados(puntos);
        asistencia.setFechaAsistencia(LocalDateTime.now());
        asistencia.setStatus("PROCESADA");
        return asistencia;
    }
}
