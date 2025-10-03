package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

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
}
