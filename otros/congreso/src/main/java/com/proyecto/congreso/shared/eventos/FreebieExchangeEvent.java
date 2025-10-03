package com.proyecto.congreso.shared.eventos;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreebieExchangeEvent {
    private String movementType;
    private Long movementId;
    private String sourceFreebieId;
    private Long targetParticipantId;
    private Long passId;
    private Integer amountPoints;
    private Integer pointsAfter;
    private LocalDateTime timestamp;

}
