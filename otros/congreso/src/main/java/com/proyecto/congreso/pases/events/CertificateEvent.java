package com.proyecto.congreso.pases.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateEvent {
    private Long passId;
    private static LocalDateTime createdAt;

    public CertificateEvent(Long passId, LocalDateTime now) {
    }
}
