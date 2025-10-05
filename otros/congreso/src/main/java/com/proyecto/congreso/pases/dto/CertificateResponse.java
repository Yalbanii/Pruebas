package com.proyecto.congreso.pases.dto;

import com.proyecto.congreso.pases.model.Certificate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponse {

    private Long certificateId;
    private Long passId;
    private Long participantId;
    private String participantEmail;
    private String participantName;
    private Integer pointsAchieved;
    private Boolean reached;
    private String certificateCode;
    private LocalDateTime createdAt;

    public static CertificateResponse fromEntity(Certificate certificate) {
        return new CertificateResponse(
                certificate.getCertificateId(),
                certificate.getPassId(),
                certificate.getParticipantId(),
                certificate.getParticipantEmail(),
                certificate.getParticipantName(),
                certificate.getPointsAchieved(),
                certificate.getReached(),
                certificate.getCertificateCode(),
                certificate.getCreatedAt()
        );
    }
}
