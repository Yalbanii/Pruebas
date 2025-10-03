package com.proyecto.congreso.points.config;

import com.proyecto.congreso.points.dto.ConferencePointsData;
import com.proyecto.congreso.points.dto.FreebiePointsData;
import com.proyecto.congreso.points.model.Conferencia;
import com.proyecto.congreso.points.model.Freebies;
import com.proyecto.congreso.points.repository.ConferenceRepository;
import com.proyecto.congreso.points.repository.FreebieRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PointsDataInitializer {
    private final FreebieRepository repository;
    private final ConferenceRepository conferenceRepository;


    public PointsDataInitializer(FreebieRepository repository, ConferenceRepository conferenceRepository) {
        this.repository = repository;
        this.conferenceRepository = conferenceRepository;
    }

    @EventListener
    public void handleFreebieDataImport(FreebieDataImportedEvent event) {
        FreebiePointsData data = event.data();

        // 1. Verificar si ya existe para evitar duplicados
        Optional<Freebies> existing = repository.findById(data.getFreebieId());
        if (existing.isPresent()) {
            return; // Ya cargado
        }

        //Convertir DTO a Entidad Freebie
        Freebies freebie = new Freebies();
        freebie.setFreebieId(data.getFreebieId());
        freebie.setArticulo(data.getArticulo());
        freebie.setCosto(data.getCosto());
        freebie.setStockActual(data.getStockActual()); // Usa el stock inicial
        repository.save(freebie);
    }

    @EventListener
    public void handleConferenceDataImport(ConferenceDataImportedEvent event) {
        ConferencePointsData data = event.data();

        // 1. Verificar si ya existe para evitar duplicados
        Optional<Conferencia> existing = conferenceRepository.findById(data.getConferenciaId());
        if (existing.isPresent()) {
            return; // Ya cargado
        }

        //Convertir DTO a Entidad Conferencia
        Conferencia conferencia = new Conferencia();
        conferencia.setConferenciaId(data.getConferenciaId());
        conferencia.setPuntos(data.getPuntos());
        conferenceRepository.save(conferencia);
    }

}
