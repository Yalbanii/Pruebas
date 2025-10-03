package com.proyecto.congreso.shared.eventos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequestedEvent{
    Long passId;
    Long freebieId;
}
