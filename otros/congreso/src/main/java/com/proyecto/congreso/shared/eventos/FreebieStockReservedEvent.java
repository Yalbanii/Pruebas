package com.proyecto.congreso.shared.eventos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreebieStockReservedEvent{
    Long passId;
    Long freebieId;
    Integer costo;

}
