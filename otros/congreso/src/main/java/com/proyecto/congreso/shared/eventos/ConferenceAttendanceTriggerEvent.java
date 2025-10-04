package com.proyecto.congreso.shared.eventos;

// Dispara la lógica para buscar los puntos que otorga la conferencia
public record ConferenceAttendanceTriggerEvent(Long passId, Long conferenciaId) {
}
