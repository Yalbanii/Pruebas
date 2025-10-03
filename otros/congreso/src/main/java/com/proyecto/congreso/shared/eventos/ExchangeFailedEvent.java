package com.proyecto.congreso.shared.eventos;

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
