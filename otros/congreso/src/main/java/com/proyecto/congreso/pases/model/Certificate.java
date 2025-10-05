package com.proyecto.congreso.pases.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "certificados")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "pass_id", nullable = false)
    private Long passId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "participant_email", length = 100)
    private String participantEmail;

    @Column(name = "participant_name", length = 150)
    private String participantName;

    @Column(name = "points_achieved", nullable = false)
    private Integer pointsAchieved;

    @Column(name = "reached", nullable = false)
    private Boolean reached = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "certificate_code", unique = true, length = 50)
    private String certificateCode;
    //Factory method
    public static Certificate create(
            Long passId,
            Long participantId,
            String participantEmail,
            String participantName,
            Integer pointsAchieved) {

        Certificate certificate = new Certificate();
        certificate.setPassId(passId);
        certificate.setParticipantId(participantId);
        certificate.setParticipantEmail(participantEmail);
        certificate.setParticipantName(participantName);
        certificate.setPointsAchieved(pointsAchieved);
        certificate.setReached(true);

        certificate.setCertificateCode(
                generateCertificateCode(passId, participantId));

        return certificate;
    }

    // Codigo unico del certificado Formato: CERT-{PASS_ID}-{PARTICIPANT_ID}-{TIMESTAMP}
    private static String generateCertificateCode(Long passId, Long participantId) {
        long timestamp = System.currentTimeMillis();
        return String.format("CERT-%d-%d-%d", passId, participantId, timestamp);
    }
}

