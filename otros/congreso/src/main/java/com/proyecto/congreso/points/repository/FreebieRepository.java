package com.proyecto.congreso.points.repository;

import com.proyecto.congreso.points.model.Freebies;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreebieRepository extends MongoRepository<Freebies, Long> {
}
