package com.proyecto.congreso.points.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Document(collection = "freebies")
public class Freebies {
    @Id
    @Column(name = "ID_FREEBIE")
    private String id;

    private Long freebieId;

    @Column(name = "ARTICULO")
    private String articulo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "STOCK_INICIAL")
    private Integer stockInicial;

    @Column(name = "COSTO")
    private Integer costo;

    @Column(name = "ACTUAL")
    private Integer stockActual;

}
