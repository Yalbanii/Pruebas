package com.proyecto.congreso.shared.eventos;

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


    /**
     * ID del Pass del que se reduciran los puntos
     */
    private Long passId;

    /**
     * ID del Freebie que intercambio
     */
    private String freebieId;

    /**
     * Nombre del articulo (para logs)
     */
    private String articulo;

    /**
     * Cantidad de puntos que va a reducir
     */
    private Integer costo;

    /**
     * Timestamp del evento
     */
    private LocalDateTime timestamp;

    /**
     * Constructor simplificado que establece el timestamp automáticamente
     */
    public ExchangeRegisteredEvent(Long passId, String freebieId,
                                     String articulo, Integer costo) {
        this.passId = passId;
        this.freebieId = freebieId;
        this.articulo = articulo;
        this.costo = costo;
        this.timestamp = LocalDateTime.now();
    }
}
