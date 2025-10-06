package com.proyecto.congreso.points.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePointsData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String exchangeId;

    private Long passId;

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
