package com.proyecto.congreso.notification.controller;

import com.proyecto.congreso.notification.model.MovementPointsLog;
import com.proyecto.congreso.notification.service.MovementPointsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Movement Logs", description = "API para gestionar logs de movimientos de puntos en MongoDB")
@RestController
@RequestMapping("/api/movimientos-logs")
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class MovementPointsLogController {

    private final MovementPointsLogService movementPointsLogService;

    @Operation(summary = "Obtener todos los logs de movimientos de puntos")
    @GetMapping
    public ResponseEntity<List<MovementPointsLog>> getAllMovementPointsLog() {
        return ResponseEntity.ok(movementPointsLogService.getAllMovementPointsLog());
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MovementPointsLog> getMovementPointsLogById(@PathVariable String id) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogById(id));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por número de pase")
    @GetMapping("/pases/{passId}")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogBypassId(@PathVariable Long passId) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByPassId(passId));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por número de Pase ordenados por fecha")
    @GetMapping("/pases/{passId}/ordered")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByPassIdOrderedByDate(@PathVariable Long passId) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByPassIdOrderedByDate(passId));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por tipo de movimiento")
    @GetMapping("/movimiento-type/{movementType}")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByType(@PathVariable String movementType) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByMovementType(movementType));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por participante")
    @GetMapping("/participantes/{participantId}")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByCustomer(@PathVariable Long participantId) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByParticipantId(participantId));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por estado")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByStatus(@PathVariable String status) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByStatus(status));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por rango de fechas")
    @GetMapping("/date-range")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByDateRange(startDate, endDate));
    }

    @Operation(summary = "Obtener logs de movimientos de puntos por Pase y rango de fechas")
    @GetMapping("/pases/{passId}/date-range")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByAccountAndDateRange(
            @PathVariable Long passId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByPassIdAndDateRange(passId, startDate, endDate));
    }

    @Operation(summary = "Obtener transaction logs por puntos")
    @GetMapping("/points")
    public ResponseEntity<List<MovementPointsLog>> getMovementPointsLogByPoints(
            @RequestParam Integer puntos) {
        return ResponseEntity.ok(movementPointsLogService.getMovementPointsLogByPoints(puntos));
    }

    @Operation(summary = "Contar logs de movimientos de puntos por tipo de movimientos")
    @GetMapping("/count/movimiento-type/{movementType}")
    public ResponseEntity<Long> countByMovementType(@PathVariable String movementType) {
        return ResponseEntity.ok(movementPointsLogService.countByMovementType(movementType));
    }

    @Operation(summary = "Contar logs de movimientos de puntos por estado")
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable String status) {
        return ResponseEntity.ok(movementPointsLogService.countByStatus(status));
    }

    @Operation(summary = "Contar logs de movimientos de puntos por Pase")
    @GetMapping("/count/pases/{passId}")
    public ResponseEntity<Long> countByPassId(@PathVariable Long passId) {
        return ResponseEntity.ok(movementPointsLogService.countByPassId(passId));
    }

    @Operation(summary = "Crear logs de movimientos de puntos manual")
    @PostMapping
    public ResponseEntity<MovementPointsLog> createMovementPointsLog(@RequestBody MovementPointsLog movementPointsLog) {
        return ResponseEntity.ok(movementPointsLogService.createMovementPointsLog(movementPointsLog));
    }

    @Operation(summary = "Eliminar logs de movimientos de puntos")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovementPointsLog(@PathVariable String id) {
        movementPointsLogService.deleteMovementPointsLog(id);
        return ResponseEntity.noContent().build();
    }
}
