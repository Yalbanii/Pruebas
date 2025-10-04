package com.proyecto.congreso.asistencia.repository;

import com.proyecto.congreso.asistencia.model.Freebies;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreebieRepository extends MongoRepository<Freebies, Long> {
}
