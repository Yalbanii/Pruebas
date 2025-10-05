package com.proyecto.congreso.points.assistance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistanceRequest {

    @NotNull(message = "Pass ID es requerido")
    private Long passId;

    @NotNull(message = "Conferencia ID es requerido")
    private String conferenciaId;

}
