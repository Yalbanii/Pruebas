package com.proyecto.congreso.points.exchange.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRegisteredEvent {

    private Long passId;

    private String freebieId;

    private String articulo;

    private Integer costo;

    private LocalDateTime timestamp;


    public ExchangeRegisteredEvent(Long passId, String freebieId,
                                     String articulo, Integer costo) {
        this.passId = passId;
        this.freebieId = freebieId;
        this.articulo = articulo;
        this.costo = costo;
        this.timestamp = LocalDateTime.now();
    }
}
