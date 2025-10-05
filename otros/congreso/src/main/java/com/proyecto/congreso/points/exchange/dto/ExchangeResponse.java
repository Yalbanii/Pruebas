package com.proyecto.congreso.points.exchange.dto;

import com.proyecto.congreso.points.exchange.model.Exchange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponse {
    private String id;
    private Long passId;
    private Long participantId;
    private String freebieId;
    private String articulo;
    private Integer costo;
    private LocalDateTime fechaIntercambio;

    public static ExchangeResponse fromEntity(Exchange exchange) {
        return new ExchangeResponse(
                exchange.getId(),
                exchange.getPassId(),
                exchange.getParticipantId(),
                exchange.getFreebieId(),
                exchange.getArticulo(),
                exchange.getPuntosReducidos(),
                exchange.getFechaIntercambio()
        );
    }
}
