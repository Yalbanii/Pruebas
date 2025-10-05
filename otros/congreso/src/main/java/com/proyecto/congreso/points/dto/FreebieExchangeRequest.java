package com.proyecto.congreso.points.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar el intercambio de puntos por un Freebie.
 * Similar a AssistanceRequest pero para el flujo de descuento de puntos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreebieExchangeRequest {


    @NotNull(message = "Pass ID es requerido")
    private Long passId;

    @NotNull(message = "Freebie ID es requerido")
    private String freebieId;

}
