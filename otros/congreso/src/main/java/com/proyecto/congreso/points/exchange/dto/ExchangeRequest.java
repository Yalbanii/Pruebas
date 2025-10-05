package com.proyecto.congreso.points.exchange.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {


    @NotNull(message = "Pass ID es requerido")
    private Long passId;

    @NotNull(message = "Freebie ID es requerido")
    private String freebieId;

}
