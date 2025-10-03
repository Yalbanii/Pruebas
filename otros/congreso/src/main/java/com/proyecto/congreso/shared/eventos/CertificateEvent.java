package com.proyecto.congreso.shared.eventos;

import java.time.LocalDateTime;


public record CertificateEvent(Long passId){
    private static Long participantId;
    private static String fullName;
    private static String email;
    private static Long certificateId;
    private static LocalDateTime createdAt;
}
