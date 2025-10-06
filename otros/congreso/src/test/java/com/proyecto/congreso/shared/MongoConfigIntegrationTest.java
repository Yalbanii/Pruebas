package com.proyecto.congreso.shared;

import com.proyecto.congreso.points.exchange.repository.ExchangeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Usamos @SpringBootTest para cargar el contexto completo de Spring.
// Se recomienda usar un perfil de prueba para configurar una base de datos de prueba (ej. Embedded Mongo)
@SpringBootTest
@ActiveProfiles("test-mongo")
public class MongoConfigIntegrationTest {

    // 💡 Inyectamos una dependencia clave para verificar que la configuración se cargó
    @Autowired
    private MongoTemplate mongoTemplate;

    // 💡 Inyectamos un repositorio que está listado en el basePackages de la configuración
    // Nota: Reemplaza "ExchangeRepository" por un repositorio real que uses en uno de esos paquetes.
    @Autowired
    private ExchangeRepository exchangeRepository;

    /**
     * Confirma que el MongoTemplate se ha inicializado correctamente.
     * Esto verifica que la condición @ConditionalOnBean(MongoTemplate.class) se cumplió.
     */
    @Test
    void mongoTemplate_shouldBeInitialized() {
        assertNotNull(mongoTemplate, "El MongoTemplate debe estar disponible en el contexto de Spring.");
    }

    /**
     * Confirma que los repositorios de MongoDB se han escaneado e inicializado.
     * Esto verifica que @EnableMongoRepositories funcionó correctamente.
     */
    @Test
    void mongoRepositories_shouldBeScannedAndInjected() {
        assertNotNull(exchangeRepository, "El repositorio de MongoDB debe haber sido encontrado e inyectado.");
    }
}