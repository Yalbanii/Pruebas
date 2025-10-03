package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.Pass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassResponse {
    private Long passId;
    private Pass.PassType passType;
    private Integer pointsBalance = 0;
    private Long participantId;
    private Pass.PassStatus status;
    private Pass.AccessStatus accessStatus;
    private Pass.CertificateStatus certificateStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PassResponse fromEntity(Pass pass) {
        return new PassResponse(
                pass.getPassId(),
                pass.getPassType(),
                pass.getPointsBalance(),
                pass.getParticipantId(),
                pass.getStatus(),
                pass.getAccessStatus(),
                pass.getCertificateStatus(),
                pass.getCreatedAt(),
                pass.getUpdatedAt()
        );
    }
}
