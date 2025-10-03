package com.proyecto.congreso.points.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferencePointsData {
    private Long conferenciaId;
    private String dia;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String duracion;
    private String sede;
    private String tipo;
    private String titulo;
    private String ponente;
    private Integer puntos;
}
