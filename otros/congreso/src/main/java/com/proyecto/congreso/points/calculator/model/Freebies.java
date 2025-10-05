package com.proyecto.congreso.points.calculator.model;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "freebies")
public class Freebies {
    @Id
    private String freebieId;  // MongoDB genera IDs de tipo String

    @Field("articulo")
    private String articulo;

    @Field("descripcion")
    private String descripcion;

    @Field("stock_inicial")
    private Integer stockInicial;

    @Field("costo")
    private Integer costo;

    @Field("stock_actual")
    private Integer stockActual;
}
