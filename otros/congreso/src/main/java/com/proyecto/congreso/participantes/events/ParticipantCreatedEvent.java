package com.proyecto.congreso.participantes.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantCreatedEvent {
    private Long participantId;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;

}
