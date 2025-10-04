package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.shared.eventos.AssistancePointsEvent;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public class PassEventHandler {
    private final PassRepository passRepository;

    public PassEventHandler(PassRepository passRepository) {
        this.passRepository = passRepository;
    }

//    // Este método escucha el evento y se ejecuta cuando se publica
//    @EventListener
//    @Async
//    public void handlePassDataImport(PassDataImportedEvent event) {
//        // 1. Recibir los datos del evento
//        String data = event.getRawCsvData();
//
//        // 2. Aplicar la lógica de negocio (validación, transformación)
//        Pass newPass = transformDataToPass(data);
//
//        // 3. Guardar el objeto Pass en MongoDB
//        passRepository.save(newPass);
//    }

    // ... Método de transformación ...

    @EventListener
    @Async
    @Transactional
    public void handlePointsApplication(AssistancePointsEvent event) {
        // 1. Acceso a MYSQL (a través del PassRepository)
        //    Tu PassRepository debe ser un JpaRepository.
        Optional<Pass> optionalPass = passRepository.findById(event.getPassId());

        // ... (Manejo de Optional) ...

        // 2. Lógica de actualización del Pass en MySQL
        Pass pass = optionalPass.get();
        Integer puntosASumar = event.getAmountPoints(); // ¡Este dato vino de Mongo via evento!

        // Actualiza y guarda en MySQL
        pass.setPointsBalance(pass.getPointsBalance() + puntosASumar);
        passRepository.save(pass);

        // ... (Publicación de CertificateEvent si se alcanza el umbral)
    }
}
