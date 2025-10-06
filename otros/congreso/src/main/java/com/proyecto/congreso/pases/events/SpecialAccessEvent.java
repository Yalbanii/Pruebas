package com.proyecto.congreso.pases.events;


import java.time.LocalDateTime;

public record SpecialAccessEvent(
        Long passId,
        Long participantId,
        String fullName,
        String email,
        String accessId,
        LocalDateTime createdAt
) {

    public SpecialAccessEvent(Long passId, Long participantId, String fullName, String email) {
        this(
                passId,
                participantId,
                fullName,
                email,
                generateAccessId(passId, participantId),
                LocalDateTime.now()
        );
    }

    private static String generateAccessId(Long passId, Long participantId) {
        long timestamp = System.currentTimeMillis();
        return String.format("ACCESS-%d-%d-%d", passId, participantId, timestamp);
    }
}