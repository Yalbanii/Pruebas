package com.proyecto.congreso.points.assistance.controller;

import com.proyecto.congreso.points.assistance.dto.AssistanceRequest;
import com.proyecto.congreso.points.assistance.dto.AssistanceResponse;
import com.proyecto.congreso.points.assistance.service.AssistanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Tag(name = "Asistencias", description = "Gestión de asistencias a conferencias")
public class AsistenciaSimuladorController {
    private final AssistanceService assistanceService;

    @PostMapping("/marcar")
    @Operation(summary = "Marcar asistencia a una conferencia",
            description = "Registra la asistencia de un Pass a una conferencia y suma los puntos correspondientes")
    public ResponseEntity<AssistanceResponse> marcarAsistencia(
            @Valid @RequestBody AssistanceRequest request) {

        AssistanceResponse response = assistanceService.marcarAsistencia(
                request.getPassId(),
                request.getConferenciaId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pass/{passId}")
    @Operation(summary = "Obtener historial de asistencias de un Pass")
    public ResponseEntity<List<AssistanceResponse>> getAsistenciasByPass(
            @PathVariable Long passId) {
        List<AssistanceResponse> asistencias = assistanceService.getAsistenciasByPass(passId);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/conferencia/{conferenciaId}")
    @Operation(summary = "Obtener lista de asistentes a una conferencia")
    public ResponseEntity<List<AssistanceResponse>> getAsistenciasByConferencia(
            @PathVariable String conferenciaId) {
        List<AssistanceResponse> asistencias = assistanceService.getAsistenciasByConferencia(conferenciaId);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/pass/{passId}/total-puntos")
    @Operation(summary = "Calcular total de puntos acumulados por asistencias")
    public ResponseEntity<Integer> getTotalPuntosAcumulados(@PathVariable Long passId) {
        Integer totalPuntos = assistanceService.getTotalPuntosAcumulados(passId);
        return ResponseEntity.ok(totalPuntos);
    }

    @GetMapping("/pass/{passId}/count")
    @Operation(summary = "Contar asistencias de un Pass")
    public ResponseEntity<Long> countAsistenciasByPass(@PathVariable Long passId) {
        long count = assistanceService.countAsistenciasByPass(passId);
        return ResponseEntity.ok(count);
    }
}
