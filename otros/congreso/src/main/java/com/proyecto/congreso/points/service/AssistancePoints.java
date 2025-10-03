package com.proyecto.congreso.points.service;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Slf4j // Para usar 'log.error'
@RequiredArgsConstructor
public class AssistancePoints {
    private final PassRepository passRepository;

    @EventListener
    @Async
    @Transactional
    public void handlePointsApplication(AssistancePointsEvent event) {
        Optional<Pass> optionalPass = passRepository.findById(event.getPassId());

        if (optionalPass.isEmpty()) {
            log.error("Fallo de puntos: Pass ID {} no encontrado para sumar puntos.", event.getPassId());
            return;
        }

        Pass pass = optionalPass.get();

        // 1. Sumar Puntos
        Integer puntosActuales = pass.getPointsBalance();
        Integer puntosASumar = event.getPointsAfter(); // El evento debe llevar este dato

        pass.setPointsBalance(puntosActuales + puntosASumar);
        pass.setUpdatedAt(LocalDateTime.now());

        passRepository.save(pass);

        log.info("✅ Puntos sumados a Pass ID {}: {} (Nuevo Balance: {})",
                pass.getPassId(), puntosASumar, pass.getPointsBalance());

        // NOTA: Si necesitas publicar un evento de Auditoría, hazlo aquí.

}
}
