package com.proyecto.congreso.points.calculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(collection = "conferencias")
public class Conferencia {
    @Id
    private String conferenciaId;

    @Field(name = "DIA")
    private String dia;

    @Field(name = "FECHA")
    private String fecha;

    @Field(name = "HORA_INICIO")
    private String horaInicio;

    @Field(name = "HORA_FIN")
    private String horaFin;

    @Field(name = "DURACION")
    private String duracion;

    @Field(name = "SEDE")
    private String sede;

    @Field(name = "TIPO")
    private String tipo;

    @Field(name = "TITULO")
    private String titulo;

    @Field(name = "PONENTE")
    private String ponente;

    @Field(name = "PUNTOS")
    private Integer puntos;
}

