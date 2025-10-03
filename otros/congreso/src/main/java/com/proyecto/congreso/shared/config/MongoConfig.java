package com.proyecto.congreso.shared.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuración de MongoDB.
 * Solo se activa cuando MongoTemplate está disponible.
 */
@Configuration
@ConditionalOnBean(MongoTemplate.class)
@EnableMongoRepositories(basePackages = {
    "com.proyecto.congreso.notification.repository",
    "com.proyecto.congreso.batch.repository"
})
public class MongoConfig {
}