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
@Table(name = "accesos_especiales")
public class SpecialAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_id")
    private Long accessId;

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

    @Column(name = "access_code", unique = true, length = 50)
    private String accessCode;

    //Factory method para crear un nuevo acceso especial

    public static SpecialAccess create(
            Long passId,
            Long participantId,
            String participantEmail,
            String participantName,
            Integer pointsAchieved) {

        SpecialAccess access = new SpecialAccess();
        access.setPassId(passId);
        access.setParticipantId(participantId);
        access.setParticipantEmail(participantEmail);
        access.setParticipantName(participantName);
        access.setPointsAchieved(pointsAchieved);
        access.setReached(true);
        access.setAccessCode(generateAccessCode(passId, participantId));

        return access;
    }
    private static String generateAccessCode(Long passId, Long participantId) {
        long timestamp = System.currentTimeMillis();
        return String.format("ACCESS-%d-%d-%d", passId, participantId, timestamp);
    }
}
