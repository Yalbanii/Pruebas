package com.proyecto.congreso.points.service;

import com.proyecto.congreso.points.exchange.events.ExchangeFailedEvent;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FreebieStockHandler {

    private final FreebieRepository freebieRepository;
    private final ApplicationEventPublisher events;

    @EventListener
    @Transactional
    public void handleExchangeFailed(ExchangeFailedEvent event) {
        log.warn(" Evento recibido: ExchangeFailedEvent - Pass={}, Razón={}",
                event.getPassId(), event.getReason());

        if (!Boolean.TRUE.equals(event.getShouldRevertStock()) || event.getFreebieId() == null) {
            log.debug("ℹ️ No hay stock que revertir para este tipo de fallo");
            return;
        }

        try {
            // Revertir el stock
            freebieRepository.findById(event.getFreebieId()).ifPresentOrElse(
                    freebie -> {
                        Integer stockAnterior = freebie.getStockActual();
                        freebie.setStockActual(stockAnterior + 1);
                        freebieRepository.save(freebie);

                        log.info("✅ Stock revertido para Freebie ID {}: {} → {}",
                                event.getFreebieId(), stockAnterior, freebie.getStockActual());
                    },
                    () -> log.warn("⚠️ Freebie ID {} no encontrado. No se pudo revertir stock.",
                            event.getFreebieId())
            );

        } catch (Exception e) {
            log.error("❌ Error al revertir stock: {}", event, e);
        }
    }

    // Método auxiliar para reducir stock directamente usado por ExchangeService, es llamado directamente, no es LISTENER
    @Transactional
    public boolean reduceStock(String freebieId) {
        log.info(" Reduciendo stock de Freebie ID: {}", freebieId);

        return freebieRepository.findById(freebieId)
                .map(freebie -> {
                    if (freebie.getStockActual() <= 0) {
                        log.warn("⚠️ Stock insuficiente para Freebie ID: {}", freebieId);
                        return false;
                    }

                    Integer stockAnterior = freebie.getStockActual();
                    freebie.setStockActual(stockAnterior - 1);
                    freebieRepository.save(freebie);

                    log.info("✅ Stock reducido para Freebie ID {}: {} → {}",
                            freebieId, stockAnterior, freebie.getStockActual());

                    return true;
                })
                .orElseGet(() -> {
                    log.error("❌ Freebie ID {} no encontrado", freebieId);
                    return false;
                });
    }
}