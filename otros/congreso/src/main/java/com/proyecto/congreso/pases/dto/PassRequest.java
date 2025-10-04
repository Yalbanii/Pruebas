package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.Pass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassRequest {

    @NotNull(message = "Participant ID is required")
    private Long participantId;

    @NotNull(message = "Pass type is required")
    private Pass.PassType passType;

    private Integer pointsBalance = 0;
}
