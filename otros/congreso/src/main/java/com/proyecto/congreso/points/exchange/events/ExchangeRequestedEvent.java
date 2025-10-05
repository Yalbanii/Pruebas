package com.proyecto.congreso.points.exchange.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequestedEvent{
    Long passId;
    String freebieId;
}
