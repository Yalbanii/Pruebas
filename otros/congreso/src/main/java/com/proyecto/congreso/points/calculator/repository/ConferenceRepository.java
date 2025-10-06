package com.proyecto.congreso.points.calculator.repository;

import com.proyecto.congreso.points.calculator.model.Conferencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository  extends MongoRepository<Conferencia, String> {
}
