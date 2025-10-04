package com.proyecto.congreso.asistencia.config;

import com.proyecto.congreso.asistencia.dto.ConferencePointsData;
import com.proyecto.congreso.asistencia.model.FreebiePointsData;
import com.proyecto.congreso.asistencia.model.Conferencia;
import com.proyecto.congreso.asistencia.model.Freebies;
import com.proyecto.congreso.asistencia.repository.ConferenceRepository;
import com.proyecto.congreso.asistencia.repository.FreebieRepository;
import com.proyecto.congreso.shared.eventos.ConferenceDataImportedEvent;
import com.proyecto.congreso.shared.eventos.FreebieDataImportedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
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
        freebie.setStockActual(data.getStockActual());
        repository.save(freebie);

        log.debug("✅ Freebie guardado: ID={}, Articulo={}", data.getFreebieId(), data.getArticulo());
    }

    @EventListener
    public void handleConferenceDataImport(ConferenceDataImportedEvent event) {
        ConferencePointsData data = event.data();

        // 1. Verificar si ya existe para evitar duplicados
        Optional<Conferencia> existing = conferenceRepository.findById(data.getConferenciaId());
        if (existing.isPresent()) {
            return; // Ya cargado
        }

        //Convertir DTO a Entidad Conferencia CON TODOS LOS CAMPOS
        Conferencia conferencia = new Conferencia();
        conferencia.setConferenciaId(data.getConferenciaId());
        conferencia.setTitulo(data.getTitulo());  //Guardar el título
        conferencia.setPuntos(data.getPuntos());
        conferencia.setDia(data.getDia());
        conferencia.setSede(data.getSede());
        conferencia.setTipo(data.getTipo());
        conferencia.setPonente(data.getPonente());

        conferenceRepository.save(conferencia);

        log.debug("✅ Conferencia guardada: ID={}, Titulo={}, Puntos={}",
                data.getConferenciaId(), data.getTitulo(), data.getPuntos());
    }
}