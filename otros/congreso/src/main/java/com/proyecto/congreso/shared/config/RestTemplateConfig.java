package com.proyecto.congreso.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de RestTemplate para comunicación entre módulos via REST.
 *
 * Se usa para mantener bajo acoplamiento entre módulos:
 * - El módulo 'asistencia' consulta datos del módulo 'pases' sin conocer sus repositorios
 * - Alternativa más desacoplada que inyectar repositorios directamente
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
