package com.proyecto.congreso.points.exchange.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeFailedEvent{
    Long passId;
    String reason;

}
