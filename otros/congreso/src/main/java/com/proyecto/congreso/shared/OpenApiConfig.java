package com.proyecto.congreso.shared;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuracion de OpenAPI/Swagger para la documentacion de la API.

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Congreso Sistema Interactivo API")
                        .version("1.0")
                        .description("API REST para el Sistema de Congreso Interactivo\n\n" +
                                "**Módulos disponibles:**\n" +
                                "- Participantes: Gestión de participantes del congreso\n" +
                                "- Pases: Gestión de pases y puntos\n" +
                                "- Asistencias: Registro de asistencia a conferencias (suma puntos)\n" +
                                "- Freebies: Gestión de artículos e intercambio por puntos (resta puntos)\n" +
                                "- Notificaciones\n")                        .contact(new Contact()
                                .name("Congreso")
                                .email("support@congreso.com")));
    }


    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan(
                        "com.proyecto.congreso.pases.controller",
                        "com.proyecto.congreso.participantes.controller",
                         "com.proyecto.congreso.points.assistance.controller",
                        "com.proyecto.congreso.points.exchange.controller",
                        "com.proyecto.congreso.notification.controller"
                )
                .build();
    }
        @Bean
    public GroupedOpenApi participantesApi() {
        return GroupedOpenApi.builder()
                .group("participantes")
                .displayName("Módulo de Participantes")
                .packagesToScan("com.proyecto.congreso.participantes.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi pasesApi() {
        return GroupedOpenApi.builder()
                .group("pases")
                .displayName("Módulo de Pases")
                .packagesToScan("com.proyecto.congreso.pases.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi asistenciasApi() {
        return GroupedOpenApi.builder()
                .group("asistencias")
                .displayName("Módulo de Asistencias")
                .packagesToScan("com.proyecto.congreso.points.assistance.controller")
                .build();
    }
        @Bean
    public GroupedOpenApi intercambiosApi() {
        return GroupedOpenApi.builder()
                .group("intercambios")
                .displayName("Módulo de Intercambios")
                .packagesToScan("com.proyecto.congreso.points.exchange.controller")
                .build();
    }
    @Bean
    public GroupedOpenApi notificacionesApi() {
        return GroupedOpenApi.builder()
                .group("notificaciones")
                .displayName("Módulo de Notificaciones")
                .packagesToScan("com.proyecto.congreso.notification.controller")
                .build();
    }


}
