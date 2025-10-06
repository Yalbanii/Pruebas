package com.proyecto.congreso.pases.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pass {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long passId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pass_type", nullable = false, length = 20)
    private PassType passType;

    @Column(nullable = false)
    private Integer pointsBalance = 0;

    @Column(nullable = false)
    private Integer pointsCertificate = 25;

    @Column(nullable = false)
    private Integer pointsSpecialAccess = 30;

    @Enumerated(EnumType.STRING)
    @Column(name = "points_add", nullable = true, length = 20)
    private PointsMovementAdd pointsAdd;

    @Enumerated(EnumType.STRING)
    @Column(name = "points_use", nullable = true, length = 20)
    private PointsMovementUse pointsUse;

    @NotNull(message = "Participant ID is required")
    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PassStatus status = PassStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccessStatus accessStatus = AccessStatus.NOT_REACHED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CertificateStatus certificateStatus = CertificateStatus.NOT_REACHED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public enum PassType {
        GENERAL, //solo acceso
        ALL_INCLUDED //acceso, transporte y comida incluida
    }

    public enum PointsMovementAdd {
        ADD //Agregar puntos en asistencias a conferencias
    }

    public enum PointsMovementUse{
        USE //Usar puntos para cambiarlos por Freebies
    }

    public enum PassStatus {
        ACTIVE,
        CLOSED
    }

    public enum AccessStatus {
        REACHED,
        NOT_REACHED
    }

    public enum CertificateStatus {
        REACHED,
        NOT_REACHED
    }

}
