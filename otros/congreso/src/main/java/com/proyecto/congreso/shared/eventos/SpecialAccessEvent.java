package com.proyecto.congreso.shared.eventos;

import java.time.LocalDateTime;

public record SpecialAccessEvent(Long passId) {
    private static Long participantId;
    private static String fullName;
    private static String email;
    private static Long accessId;
    private static LocalDateTime createdAt;
}
