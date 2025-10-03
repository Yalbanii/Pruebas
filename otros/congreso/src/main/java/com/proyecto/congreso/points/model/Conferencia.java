package com.proyecto.congreso.points.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Document(collection = "conferencias")
public class Conferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conferenciaId;

    @Column(name = "DIA")
    private Integer dia;

    @Column(name = "FECHA")
    private Long fecha;

    @Column(name = "HORA_INICIO")
    private Long horaInicio;

    @Column(name = "HORA_FIN")
    private Long horaFin;

    @Column(name = "DURACION")
    private Long duracion;

    @Column(name = "SEDE")
    private String sede;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "PONENTE")
    private String ponente;

    @Column(name = "PUNTOS")
    private Integer puntos;
}

