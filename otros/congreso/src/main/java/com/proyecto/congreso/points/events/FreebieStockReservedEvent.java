package com.proyecto.congreso.points.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreebieStockReservedEvent{
    Long passId;
    String freebieId;
    Integer costo;

}
