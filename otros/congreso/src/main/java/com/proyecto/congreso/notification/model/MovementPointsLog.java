package com.proyecto.congreso.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "movement_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementPointsLog {
    @Id
    private String id;

    private String movementId;

    private Long participantId;

    private Long passId;

    private String movementType;

    private Integer points;

    private Integer balancePoints;

    private String status;

    private LocalDateTime timestamp;

    private Map<String, Object> metadata;

    private String description;

}
