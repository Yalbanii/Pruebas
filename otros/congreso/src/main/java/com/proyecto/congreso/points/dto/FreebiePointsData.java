package com.proyecto.congreso.points.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreebiePointsData {

    private Long freebieId;
    private String articulo;
    private String descripcion;
    private Integer stockInicial;
    private Integer costo;
    private Integer stockActual;
}
