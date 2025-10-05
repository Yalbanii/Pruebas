package com.proyecto.congreso.points.service;

import com.proyecto.congreso.shared.eventos.ExchangeFailedEvent;
import com.proyecto.congreso.shared.eventos.ExchangeRequestedEvent;
import com.proyecto.congreso.shared.eventos.FreebieStockReservedEvent;
import com.proyecto.congreso.points.model.Freebies;
import com.proyecto.congreso.points.repository.FreebieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Usa la de Spring

import java.util.Optional;
@Service
@Slf4j
@RequiredArgsConstructor
public class FreebieStockHandler {
    // Solo inyecta su propio repositorio y el publicador de eventos
    private final FreebieRepository freebieRepository;
    private final ApplicationEventPublisher events;

    @EventListener
    @Async
    @Transactional
    public void handleExchangeRequest(ExchangeRequestedEvent event) {
        Optional<Freebies> optionalFreebie = freebieRepository.findById(event.getFreebieId());

        if (optionalFreebie.isEmpty() || optionalFreebie.get().getStockActual() <= 0) {
            log.warn("Fallo de Stock: Freebie ID {} no encontrado o sin stock.", event.getFreebieId());
            events.publishEvent(new ExchangeFailedEvent(event.getPassId(), "Stock insuficiente."));
            return;
        }

        Freebies freebie = optionalFreebie.get();

        // 1. Reducir Stock
        freebie.setStockActual(freebie.getStockActual() - 1);
        freebieRepository.save(freebie);

        log.info("Stock de Freebie ID {} reservado. Costo: {}", freebie.getFreebieId(), freebie.getCosto());

        // 2. Publicar evento de éxito para que el módulo Pass descuente los puntos
        events.publishEvent(new FreebieStockReservedEvent(
                event.getPassId(),
                freebie.getFreebieId(),
                freebie.getCosto()
        ));
    }
}
