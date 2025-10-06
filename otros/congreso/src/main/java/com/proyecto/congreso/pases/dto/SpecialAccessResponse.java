package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.SpecialAccess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialAccessResponse {
    private Long accessId;
    private Long passId;
    private Long participantId;
    private String participantEmail;
    private String participantName;
    private Integer pointsAchieved;
    private Boolean reached;
    private String accessCode;
    private LocalDateTime createdAt;

    public static SpecialAccessResponse fromEntity(SpecialAccess access) {
        return new SpecialAccessResponse(
                access.getAccessId(),
                access.getPassId(),
                access.getParticipantId(),
                access.getParticipantEmail(),
                access.getParticipantName(),
                access.getPointsAchieved(),
                access.getReached(),
                access.getAccessCode(),
                access.getCreatedAt()
        );
    }
}
