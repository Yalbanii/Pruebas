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
public class ExchangeFailedEvent {

    private Long passId;
    private String freebieId;
    private String reason;
    private Boolean shouldRevertStock;
    private LocalDateTime timestamp;


    public ExchangeFailedEvent(Long passId, String reason) {
        this.passId = passId;
        this.freebieId = null;
        this.reason = reason;
        this.shouldRevertStock = false;
        this.timestamp = LocalDateTime.now();
    }

    public ExchangeFailedEvent(Long passId, String freebieId, String reason, Boolean shouldRevertStock) {
        this.passId = passId;
        this.freebieId = freebieId;
        this.reason = reason;
        this.shouldRevertStock = shouldRevertStock;
        this.timestamp = LocalDateTime.now();
    }
}