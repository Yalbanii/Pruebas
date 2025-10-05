package com.proyecto.congreso.points.events;

import lombok.*;

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
