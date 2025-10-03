package com.proyecto.congreso.points.repository;

import com.proyecto.congreso.points.model.Conferencia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConferenceRepository  extends MongoRepository<Conferencia, Long> {
}
