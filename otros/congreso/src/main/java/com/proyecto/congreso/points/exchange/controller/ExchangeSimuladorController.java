package com.proyecto.congreso.points.exchange.controller;

import com.proyecto.congreso.points.exchange.dto.ExchangeRequest;
import com.proyecto.congreso.points.exchange.dto.ExchangeResponse;
import com.proyecto.congreso.points.exchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intercambios")
@RequiredArgsConstructor
@Tag(name = "Intercambios", description = "Gestión de intercambio de puntos por Freebies del Congreso a partir del dia 4.")
public class ExchangeSimuladorController {
    private final ExchangeService exchangeService;


    @PostMapping("/descontar")
    @Operation(summary = "Descontar Puntos en un intercambio",
            description = "Registra la reduccion de puntos correspondientes al costo de un Freebie del Congreso.")
    public ResponseEntity<ExchangeResponse> crearExchange(
            @Valid @RequestBody ExchangeRequest request) {

        ExchangeResponse response = exchangeService.crearExchange(
                request.getPassId(),
                request.getFreebieId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pass/{passId}")
    @Operation(summary = "Obtener historial de intercambios de un Pass")
    public ResponseEntity<List<ExchangeResponse>> getExchangesByPass(
            @PathVariable Long passId) {
        List<ExchangeResponse> exchanges = exchangeService.getExchangesByPass(passId);
        return ResponseEntity.ok(exchanges);
    }


    @GetMapping("/pass/{passId}/total-puntos")
    @Operation(summary = "Calcular total de puntos reducidos por intercambio")
    public ResponseEntity<Integer> getTotalPuntosReducidos(@PathVariable Long passId) {
        Integer totalPuntos = exchangeService.getTotalPuntosReducidos(passId);
        return ResponseEntity.ok(totalPuntos);
    }

    @GetMapping("/pass/{passId}/count")
    @Operation(summary = "Contar intercambios de un Pass")
    public ResponseEntity<Long> countExchangeByPass(@PathVariable Long passId) {
        long count = exchangeService.countExchangesByPass(passId);
        return ResponseEntity.ok(count);
    }
}
