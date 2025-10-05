package com.proyecto.congreso.points.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePointsData implements Serializable {

    private static final long serialVersionUID = 1L;


    //ID del intercambio en MongoDB (para marcarlo como procesada)
    private String exchangeId;


    // ID del Pass en MySQL (para actualizar balance)
    private Long passId;


    //ID de la conferencia a la que asistió
    private String freebieId;

    private Integer costo;

    // Balance actual del Pass ANTES y DESPUES de sumar puntos
    private Integer currentBalance;

    private Integer newBalance;

    public ExchangePointsData(String exchangeId, Long passId, String freebieId, Integer costo, Integer currentBalance) {
        this.exchangeId = exchangeId;
        this.passId = passId;
        this.freebieId = freebieId;
        this.costo = costo;
        this.currentBalance = currentBalance;
        this.newBalance = currentBalance - costo;
    }
}
