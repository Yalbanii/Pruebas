package com.proyecto.congreso.shared.eventos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassAdquiredEvent {
    private Long passId;
    private String passType;
    private Integer pointsBalance;
    private Long participantId;
    private String participantEmail;
    private LocalDateTime createdAt;
}
