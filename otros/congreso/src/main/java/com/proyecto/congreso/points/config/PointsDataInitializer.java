package com.proyecto.congreso.points.config;

import com.proyecto.congreso.points.dto.ConferencePointsData;
import com.proyecto.congreso.points.model.FreebiePointsData;
import com.proyecto.congreso.points.model.Conferencia;
import com.proyecto.congreso.points.model.Freebies;
import com.proyecto.congreso.points.repository.ConferenceRepository;
import com.proyecto.congreso.points.repository.FreebieRepository;
import com.proyecto.congreso.shared.eventos.ConferenceDataImportedEvent;
import com.proyecto.congreso.shared.eventos.FreebieDataImportedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
public class PointsDataInitializer {
    private final FreebieRepository freebieRepository;
    private final ConferenceRepository conferenceRepository;

    public PointsDataInitializer(FreebieRepository freebieRepository,
                                 ConferenceRepository conferenceRepository) {
        this.freebieRepository = freebieRepository;
        this.conferenceRepository = conferenceRepository;
    }

    @EventListener
    @Transactional
    public void handleFreebieDataImport(FreebieDataImportedEvent event) {
        FreebiePointsData data = event.data();

        // 1. Verificar si ya existe para evitar duplicados
        Optional<Freebies> existing = freebieRepository.findById(data.getFreebieId());
        if (existing.isPresent()) {
            log.debug("⏭️ Freebie ID {} ya existe, saltando...", data.getFreebieId());
            return;
        }

        // 2. Convertir DTO a Entidad Freebie
        Freebies freebie = new Freebies();
        freebie.setFreebieId(data.getFreebieId());
        freebie.setArticulo(data.getArticulo());
        freebie.setDescripcion(data.getDescripcion());
        freebie.setStockInicial(data.getStockInicial());
        freebie.setCosto(data.getCosto());
        freebie.setStockActual(data.getStockActual());

        freebieRepository.save(freebie);

        log.info("✅ Freebie guardado: ID={}, Articulo={}, Costo={}",
                data.getFreebieId(), data.getArticulo(), data.getCosto());
    }

    @EventListener
    @Transactional
    public void handleConferenceDataImport(ConferenceDataImportedEvent event) {
        ConferencePointsData data = event.data();

        // 1. Verificar si ya existe para evitar duplicados
        Optional<Conferencia> existing = conferenceRepository.findById(data.getConferenciaId());
        if (existing.isPresent()) {
            log.debug("⏭️ Conferencia ID {} ya existe, saltando...", data.getConferenciaId());
            return;
        }

        // 2. Convertir DTO a Entidad Conferencia CON TODOS LOS CAMPOS
        Conferencia conferencia = new Conferencia();
        conferencia.setConferenciaId(data.getConferenciaId());
        conferencia.setDia(data.getDia());
        conferencia.setFecha(data.getFecha());  // String, no Long
        conferencia.setHoraInicio(data.getHoraInicio());  // String
        conferencia.setHoraFin(data.getHoraFin());  // String
        conferencia.setDuracion(data.getDuracion());  // String
        conferencia.setSede(data.getSede());
        conferencia.setTipo(data.getTipo());
        conferencia.setTitulo(data.getTitulo());
        conferencia.setPonente(data.getPonente());
        conferencia.setPuntos(data.getPuntos());

        conferenceRepository.save(conferencia);

        log.info("✅ Conferencia guardada: ID={}, Titulo='{}', Puntos={}",
                data.getConferenciaId(), data.getTitulo(), data.getPuntos());
    }
}