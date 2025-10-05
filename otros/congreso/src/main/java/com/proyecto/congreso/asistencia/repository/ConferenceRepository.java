package com.proyecto.congreso.asistencia.repository;

import com.proyecto.congreso.asistencia.model.Conferencia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConferenceRepository  extends MongoRepository<Conferencia, String> {
}
