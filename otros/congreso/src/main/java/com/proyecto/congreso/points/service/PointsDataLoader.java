package com.proyecto.congreso.points.service;

import com.proyecto.congreso.points.dto.ConferencePointsData;
import com.proyecto.congreso.points.dto.FreebiePointsData;
import com.proyecto.congreso.points.config.ConferenceDataImportedEvent;
import com.proyecto.congreso.points.config.FreebieDataImportedEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointsDataLoader implements CommandLineRunner {

    private final ApplicationEventPublisher events;

    // ---------------- FREEBIES - -----------------
    // Los datos del CSV como una constante en el código
    private static final List<FreebiePointsData> INVENTORY_DATA_FREEBIES = List.of(
            new FreebiePointsData(1L, "Libreta Reciclada", "Libreta de tapa dura con logo del congreso", 350, 10, 350),
            new FreebiePointsData(2L, "Bolígrafo Táctil", "Bolígrafo con punta de goma para pantallas táctiles", 350, 5, 350),
            new FreebiePointsData(3L, "USB 32GB (Logo)", "Memoria USB con logo y material del congreso", 300, 15, 300),
            new FreebiePointsData(4L, "Llavero 3D (Cerebro)", "Llavero de goma con forma de cerebro o chip", 150, 7, 150),
            new FreebiePointsData(5L, "Llavero 3D (Chip)", "Llavero de goma con forma de chip de IA", 150, 7, 150),
            new FreebiePointsData(6L, "Taza de Viaje", "Taza térmica reutilizable para café o té", 100, 10, 100),
            new FreebiePointsData(7L, "Stickers Set (Tecnología)", "Paquete de calcomanías relacionadas con tecnología", 200, 4, 200),
            new FreebiePointsData(8L, "Stickers Set (Psicología)", "Paquete de calcomanías relacionadas con psicología", 200, 4, 200),
            new FreebiePointsData(9L, "Bolsa de Tela (Tote Bag)", "Bolsa reutilizable con diseño minimalista", 100, 10, 100),
            new FreebiePointsData(10L, "Cuaderno de Post-its", "Set de notas adhesivas personalizadas", 100, 8, 100)
    );

    // ---------------- CONFERENCIAS + -----------------
    private static final List<ConferencePointsData> INVENTORY_DATA_CONFERENCIAS = List.of(
            new ConferencePointsData(1L, "Lunes", "01-03", "09:00", "10:00", "1.0", "Sede Central - Auditorio Principal", "Conferencia Inaugural", "La IA como herramienta de diagnóstico psicológico", "Dr. Ana Pérez", 5),
            new ConferencePointsData(2L, "Lunes", "01/03", "10:00", "11:30", "1.5", "Sede Central - Auditorio Principal", "Panel de Expertos", "Ética y privacidad en la terapia online", "Dra. Carlos Gómez,Mtro. Elena Ruiz", 3),
            new ConferencePointsData(3L, "Lunes", "01/03", "11:30", "12:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(4L, "Lunes", "01/03", "12:00", "14:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Realidad Virtual para el tratamiento de fobias", "Lic. Javier Salas", 3),
            new ConferencePointsData(5L, "Lunes", "01/03", "12:00", "14:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Uso de Wearables en la monitorización del estrés", "Msc. Laura Vega", 2),
            new ConferencePointsData(6L, "Lunes", "01/03", "14:00", "15:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData(7L, "Lunes", "01/03", "15:00", "16:30", "1.5", "Sede Oeste - Sala de Conferencias", "Seminario", "Neurotecnología y mejora cognitiva", "Dr. Marco Polo", 3),
            new ConferencePointsData(8L, "Martes", "02/03", "09:00", "11:00", "2.0", "Sede Central - Auditorio Principal", "Keynote Speaker", "Big Data en la investigación psicológica", "Dra. Sofía Torres", 3),
            new ConferencePointsData(9L, "Martes", "02/03", "11:00", "11:30", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(10L, "Martes", "02/03", "11:30", "13:30", "2.0", "Sede Norte - Aula Magna", "Taller", "Gamificación en intervenciones psicológicas con adolescentes", "Lic. Daniel Arias", 5),
            new ConferencePointsData(11L, "Martes", "02/03", "11:30", "13:30", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Psicología del consumo en plataformas digitales", "Msc. Patricia Rey", 2),
            new ConferencePointsData(12L, "Martes", "02/03", "13:30", "14:30", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData(13L, "Martes", "02/03", "14:30", "16:30", "2.0", "Sede Oeste - Sala de Conferencias", "Mesa Redonda", "Ciberacoso: Impacto y Prevención Tecnológica", "Diversos Especialistas", 3),
            new ConferencePointsData(14L, "Miércoles", "03/03", "09:00", "10:30", "1.5", "Sede Central - Auditorio Principal", "Conferencia", "El futuro de la salud mental digital", "Dr. Juan Blanco", 5),
            new ConferencePointsData(15L, "Miércoles", "03/03", "10:30", "11:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(16L, "Miércoles", "03/03", "11:00", "13:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Diseño de Chatbots Terapéuticos", "Ing. Miguel Ángel", 3),
            new ConferencePointsData(17L, "Miércoles", "03/03", "11:00", "13:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Terapia Cognitivo-Conductual asistida por apps", "Msc. Carolina Solís", 2),
            new ConferencePointsData(18L, "Miércoles", "03/03", "13:00", "14:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData(19L, "Miércoles", "03/03", "14:00", "16:00", "2.0", "Sede Oeste - Sala de Conferencias", "Panel", "Regulación emocional a través de la Neurofeedback", "Dra. Fernanda Rivas", 5),
            new ConferencePointsData(20L, "Jueves", "04/03", "09:00", "10:00", "1.0", "Sede Central - Auditorio Principal", "Conferencia", "Psicología Positiva y redes sociales", "Dr. Luis Herrera", 5),
            new ConferencePointsData(21L, "Jueves", "04/03", "10:00", "11:30", "1.5", "Sede Central - Auditorio Principal", "Panel de Expertos", "Desafíos de la telepsicología en zonas rurales", "Diversos Especialistas", 3),
            new ConferencePointsData(22L, "Jueves", "04/03", "11:30", "12:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(23L, "Jueves", "04/03", "12:00", "14:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Machine Learning para la detección temprana de trastornos", "Ing. Valeria Soto", 3),
            new ConferencePointsData(24L, "Jueves", "04/03", "12:00", "14:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Efectos psicológicos de la inmersión en mundos virtuales", "Msc. Ricardo Marín", 2),
            new ConferencePointsData(25L, "Jueves", "04/03", "14:00", "15:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData(26L, "Jueves", "04/03", "15:00", "16:30", "1.5", "Sede Oeste - Sala de Conferencias", "Seminario", "Diseño de interfaces inclusivas para la salud mental", "Dr. Jaime Pardo", 3),
            new ConferencePointsData(27L, "Viernes", "05/03", "09:00", "11:00", "2.0", "Sede Central - Auditorio Principal", "Keynote Speaker", "La tecnología al servicio de la resiliencia", "Dra. Guadalupe Castro", 5),
            new ConferencePointsData(28L, "Viernes", "05/03", "11:00", "11:30", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(29L, "Viernes", "05/03", "11:30", "13:30", "2.0", "Sede Norte - Aula Magna", "Taller", "Uso de Biofeedback y BCI en psicología clínica", "Lic. Roberto León", 3),
            new ConferencePointsData(30L, "Viernes", "05/03", "11:30", "13:30", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Impacto de los videojuegos en el desarrollo infantil", "Msc. Paula Gil", 2),
            new ConferencePointsData(31L, "Viernes", "05/03", "13:30", "14:30", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData(32L, "Viernes", "05/03", "14:30", "16:30", "2.0", "Sede Oeste - Sala de Conferencias", "Mesa Redonda", "Psicología del Trabajo en la era del teletrabajo y la robótica", "Diversos Especialistas", 5),
            new ConferencePointsData(33L, "Sábado", "06/03", "09:00", "10:30", "1.5", "Sede Central - Auditorio Principal", "Conferencia", "El cerebro conectado: de la neurociencia a la tecnología", "Dr. Pedro Salas", 5),
            new ConferencePointsData(34L, "Sábado", "06/03", "10:30", "11:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(35L, "Sábado", "06/03", "11:00", "13:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Introducción a la programación para psicólogos", "Ing. Andrea Soto", 5),
            new ConferencePointsData(36L, "Sábado", "06/03", "11:00", "13:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Inteligencia Artificial en la educación emocional", "Msc. Felipe Neri", 2),
            new ConferencePointsData(37L, "Sábado", "06/03", "13:00", "14:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData(38L, "Sábado", "06/03", "14:00", "16:00", "2.0", "Sede Oeste - Sala de Conferencias", "Panel", "Ciberseguridad y el bienestar psicológico", "Dra. Marta Vidal", 5),
            new ConferencePointsData(39L, "Domingo", "07/03", "09:00", "10:00", "1.0", "Sede Central - Auditorio Principal", "Conferencia de Clausura", "Integrando mundos: El futuro de Psicología y Tecnología", "Dr. Antonio Ramos", 3),
            new ConferencePointsData(40L, "Domingo", "07/03", "10:00", "11:30", "1.5", "Sede Central - Auditorio Principal", "Panel de Clausura", "Balance y Perspectivas del Congreso", "Comité Organizador", 5),
            new ConferencePointsData(41L, "Domingo", "07/03", "11:30", "12:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData(42L, "Domingo", "07/03", "12:00", "13:00", "1.0", "Sede Central - Auditorio Principal", "Acto de Clausura", "Entrega de Premios y Despedida", "Comité Organizador", 1),
            new ConferencePointsData(43L, "Domingo", "07/03", "13:00", "14:00", "1.0", "Sede Oeste - Sala de Conferencias", "Networking", "Vino de honor / Cóctel", "", 1)
    );


public PointsDataLoader(ApplicationEventPublisher events) {
    this.events = events;
}

@Override
public void run(String... args) throws Exception {
    System.out.println("Cargando inventario inicial de freebies y publicando eventos...");

    // Publicar un evento por cada ítem del inventario
    INVENTORY_DATA_FREEBIES.forEach(data -> {
        events.publishEvent(new FreebieDataImportedEvent(data));
    });

    System.out.println("Carga de inventario inicial finalizada. Eventos publicados.");


    System.out.println("Cargando inventario inicial de conferencias y publicando eventos...");

    // Publicar un evento por cada ítem del inventario
    INVENTORY_DATA_CONFERENCIAS.forEach(data -> {
        events.publishEvent(new ConferenceDataImportedEvent(data));
    });

    System.out.println("Carga de programa general finalizada. Eventos publicados.");
}
}