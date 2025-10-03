package com.proyecto.congreso.participantes.dto;

import com.proyecto.congreso.participantes.model.Participant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {

    private Long participantId;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String nacionality;
    private Integer age;
    private String area;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ParticipantResponse fromEntity(Participant participant) {
        return new ParticipantResponse(
                participant.getParticipantId(),
                participant.getName(),
                participant.getLastName(),
                participant.getEmail(),
                participant.getPhone(),
                participant.getNacionality(),
                participant.getAge(),
                participant.getArea(),
                participant.getStatus().name(),
                participant.getCreatedAt(),
                participant.getUpdatedAt()
        );
    }
}
