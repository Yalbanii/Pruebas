package com.proyecto.congreso.points.exchange.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.points.exchange.dto.ExchangeResponse;
import com.proyecto.congreso.points.exchange.events.ExchangeFailedEvent;
import com.proyecto.congreso.points.exchange.model.Exchange;
import com.proyecto.congreso.points.calculator.model.Freebies;
import com.proyecto.congreso.points.exchange.repository.ExchangeRepository;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.exchange.events.ExchangeRegisteredEvent;
import com.proyecto.congreso.points.service.FreebieStockHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final FreebieRepository freebieRepository;
    private final PassRepository passRepository;
    private final FreebieStockHandler stockHandler;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ExchangeResponse crearExchange(Long passId, String freebieId) {
        log.info("Iniciando intercambio: Pass={}, Freebie={}", passId, freebieId);

        // ========== VALIDACIONES ==========

        Pass pass = passRepository.findById(passId)
                .orElseThrow(() -> {
                    log.error("❌ Pass no encontrado: {}", passId);
                    return new IllegalArgumentException("Pass no encontrado: " + passId);
                });

        if (pass.getStatus() != Pass.PassStatus.ACTIVE) {
            log.error("❌ Pass {} no está activo. Estado: {}", passId, pass.getStatus());
            throw new IllegalArgumentException("El Pass no está activo");
        }

        // Validar que el freebie existe
        Freebies freebie = freebieRepository.findById(freebieId)
                .orElseThrow(() -> {
                    log.error("❌ Freebie no encontrado: {}", freebieId);
                    return new IllegalArgumentException("Freebie no encontrado: " + freebieId);
                });

        // Validar que hay stock disponible
        if (freebie.getStockActual() <= 0) {
            log.error("❌ Sin stock disponible para Freebie: {} ({})",
                    freebieId, freebie.getArticulo());
            throw new IllegalArgumentException(
                    "Stock insuficiente para el Freebie: " + freebie.getArticulo());
        }

        // Validar que el Pass tiene puntos suficientes
        if (pass.getPointsBalance() < freebie.getCosto()) {
            log.error("❌ Puntos insuficientes. Balance: {}, Costo: {}",
                    pass.getPointsBalance(), freebie.getCosto());
            throw new IllegalArgumentException(
                    String.format("Puntos insuficientes. Necesitas %d puntos, tienes %d",
                            freebie.getCosto(), pass.getPointsBalance()));
        }

        log.info("✅ Validaciones completadas exitosamente");


        // ========== REDUCIR STOCK ==========
        boolean stockReducido = stockHandler.reduceStock(freebieId);
        if (!stockReducido) {
            log.error("❌ Error al reducir stock para Freebie: {}", freebieId);
            throw new IllegalStateException("Error al reservar el freebie. Intenta nuevamente.");
        }

        log.info("✅ Stock reducido exitosamente");


        // ========== CREAR REGISTRO DE INTERCAMBIO ==========
        try {
            Exchange exchange = Exchange.crear(
                    passId,
                    pass.getParticipantId(),
                    freebieId,
                    freebie.getArticulo(),
                    freebie.getCosto()
            );

            exchange = exchangeRepository.save(exchange);
            log.info("✅ Exchange registrado en MongoDB: ID={}, ParticipantId={}",
                    exchange.getId(), pass.getParticipantId());


            // ========== PUBLICAR EVENTO PARA DESCONTAR PUNTOS ==========
            ExchangeRegisteredEvent event = new ExchangeRegisteredEvent(
                    passId,
                    freebieId,
                    freebie.getArticulo(),
                    freebie.getCosto()
            );

            eventPublisher.publishEvent(event);
            log.info("Evento ExchangeRegisteredEvent publicado: Pass={}, Puntos={}",
                    passId, freebie.getCosto());

            log.info("🎉 Intercambio completado exitosamente: Pass={}, Freebie={}, Puntos={}",
                    passId, freebieId, freebie.getCosto());

            return ExchangeResponse.fromEntity(exchange);

        } catch (Exception e) {
            log.error("❌ Error después de reducir stock. Revirtiendo...", e);

            eventPublisher.publishEvent(ExchangeFailedEvent.builder()
                    .passId(passId)
                    .freebieId(freebieId)
                    .reason("Error al crear registro de intercambio: " + e.getMessage())
                    .shouldRevertStock(true)
                    .build());

            throw new IllegalStateException(
                    "Error al completar el intercambio. El stock ha sido revertido.", e);
        }
    }

    // Obtiene el historial de intercambios de un Pass.
    @Transactional(readOnly = true)
    public List<ExchangeResponse> getExchangesByPass(Long passId) {
        log.debug("🔍 Obteniendo intercambios del Pass: {}", passId);
        return exchangeRepository.findByPassId(passId).stream()
                .map(ExchangeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    public Integer getTotalPuntosReducidos(Long passId) {
        log.debug("🔍 Calculando puntos reducidos del Pass: {}", passId);
        List<Exchange> exchanges = exchangeRepository.findExchangesProcedosByPass(passId);
        return exchanges.stream()
                .mapToInt(Exchange::getPuntosReducidos)
                .sum();
    }

    // Cuenta los intercambios de un Pass.
    @Transactional(readOnly = true)
    public long countExchangesByPass(Long passId) {
        log.debug("🔍 Contando intercambios del Pass: {}", passId);
        return exchangeRepository.countByPassId(passId);
    }
}