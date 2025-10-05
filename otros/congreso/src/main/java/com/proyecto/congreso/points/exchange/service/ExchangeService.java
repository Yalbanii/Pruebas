package com.proyecto.congreso.points.exchange.service;

import com.proyecto.congreso.points.exchange.dto.ExchangeResponse;
import com.proyecto.congreso.points.exchange.model.Exchange;
import com.proyecto.congreso.points.calculator.model.Freebies;
import com.proyecto.congreso.points.exchange.repository.ExchangeRepository;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.exchange.events.ExchangeRegisteredEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ExchangeResponse crearExchange(Long passId, String freebieId) {
        log.info("📋 Autorizando intercambio : Pass={}, Freebie={}", passId, freebieId);

        // 1. Validar que el freebie existe
        Freebies freebies = freebieRepository.findById(freebieId)
                .orElseThrow(() -> {
                    log.error("❌ Freebie no encontrado: {}", freebieId);
                    return new IllegalArgumentException("Freebie no encontrado: " + freebieId);
                });

        // 2. Obtener participantId del Pass mediante API REST
        Long participantId = getParticipantIdFromPass(passId);

        // 3. Crear registro de intercambio en MongoDB
        Exchange exchange = new Exchange();
        exchange.setPassId(passId);
        exchange.setParticipantId(participantId);
        exchange.setFreebieId(freebieId);
        exchange.setArticulo(freebies.getArticulo());
        exchange.setPuntosReducidos(freebies.getCosto());
        exchange.setFechaIntercambio(java.time.LocalDateTime.now());

        exchange = exchangeRepository.save(exchange);
        log.info("✅ Exchange registrado en MongoDB: ID={}, ParticipantId={}",
                exchange.getId(), participantId);

        // 5. Publicar evento para que el módulo 'pases' sume puntos
        ExchangeRegisteredEvent event = new ExchangeRegisteredEvent(
                passId,
                freebieId,
                freebies.getArticulo(),
                freebies.getCosto()
        );

        eventPublisher.publishEvent(event);
        log.info("📢 Evento ExchangeRegisteredEvent publicado: Pass={}, Puntos={}",
                passId, freebies.getCosto());

        return ExchangeResponse.fromEntity(exchange);
    }

    /**
     * Obtiene el participantId asociado a un Pass mediante llamada REST.
     *
     * NOTA: Usamos REST en lugar de inyectar PassRepository para mantener
     * el bajo acoplamiento entre módulos. Alternativa: Podríamos incluir
     * el participantId en el request, pero eso requeriría que el cliente
     * lo conozca, lo cual no es ideal.
     */
    Long getParticipantIdFromPass(Long passId) {
        try {
            PassResponse response = new PassResponse();

            if (response.getParticipantId() == null) {
                log.error("❌ No se pudo obtener participantId del Pass: {}", passId);
                throw new IllegalArgumentException("Pass no encontrado o sin participantId: " + passId);
            }

            log.debug("✅ ParticipantId obtenido: {} para Pass: {}",
                    response.getParticipantId(), passId);
            return response.getParticipantId();

        } catch (Exception e) {
            log.error("❌ Error al consultar Pass: {}", passId, e);
            throw new IllegalArgumentException("Error al validar Pass: " + passId, e);
        }
    }

    /**
     * Obtiene el historial de asistencias de un Pass.
     */
    @Transactional(readOnly = true)
    public List<ExchangeResponse> getExchangesByPass(Long passId) {
        log.debug("🔍 Obteniendo intercambios del Pass: {}", passId);
        return exchangeRepository.findByPassId(passId).stream()
                .map(ExchangeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el total de puntos acumulados por asistencias.
     * NOTA: Este es un cálculo informativo basado en MongoDB.
     * El balance real está en Pass (MySQL).
     */
    @Transactional(readOnly=true)
    public Integer getTotalPuntosReducidos(Long passId) {
        log.debug("🔍 Calculando puntos reducidos del Pass: {}", passId);
        List<Exchange> exchanges = exchangeRepository.findExchangesProcedosByPass(passId);
        return exchanges.stream()
                .mapToInt(Exchange::getPuntosReducidos)
                .sum();
    }

    /**
     * Cuenta las asistencias de un Pass.
     */
    @Transactional(readOnly = true)
    public long countExchangesByPass(Long passId) {
        log.debug("🔍 Contando intercambios del Pass: {}", passId);
        return exchangeRepository.countByPassId(passId);
    }

    /**
     * Clase interna para mapear la respuesta del Pass API.
     * Solo incluye los campos que necesitamos.
     */
    private static class PassResponse {
        private Long passId;
        private Long participantId;

        public Long getPassId() { return passId; }
        public void setPassId(Long passId) { this.passId = passId; }

        public Long getParticipantId() { return participantId; }
        public void setParticipantId(Long participantId) { this.participantId = participantId; }
    }
}
