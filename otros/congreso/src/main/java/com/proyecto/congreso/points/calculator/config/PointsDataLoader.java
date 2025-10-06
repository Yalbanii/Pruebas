package com.proyecto.congreso.points.calculator.config;

import com.proyecto.congreso.points.calculator.dto.ConferencePointsData;
import com.proyecto.congreso.points.calculator.model.FreebiePointsData;
import com.proyecto.congreso.points.calculator.events.ConferenceDataImportedEvent;
import com.proyecto.congreso.points.calculator.events.FreebieDataImportedEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointsDataLoader implements CommandLineRunner {

    private final ApplicationEventPublisher events;

    // ---------------- FREEBIES - -----------------
    // Los datos del CSV como una constante en el codigo
    private static final List<FreebiePointsData> INVENTORY_DATA_FREEBIES = List.of(
            new FreebiePointsData("1", "Libreta Reciclada", "Libreta de tapa dura con logo del congreso", 350, 10, 350),
            new FreebiePointsData("2", "Bolígrafo Táctil", "Bolígrafo con punta de goma para pantallas táctiles", 350, 5, 350),
            new FreebiePointsData("3", "USB 32GB (Logo)", "Memoria USB con logo y material del congreso", 300, 15, 300),
            new FreebiePointsData("4", "Llavero 3D (Cerebro)", "Llavero de goma con forma de cerebro o chip", 150, 7, 150),
            new FreebiePointsData("5", "Llavero 3D (Chip)", "Llavero de goma con forma de chip de IA", 150, 7, 150),
            new FreebiePointsData("6", "Taza de Viaje", "Taza térmica reutilizable para café o té", 100, 10, 100),
            new FreebiePointsData("7",  "Stickers Set (Tecnología)", "Paquete de calcomanías relacionadas con tecnología", 200, 4, 200),
            new FreebiePointsData("8", "Stickers Set (Psicología)", "Paquete de calcomanías relacionadas con psicología", 200, 4, 200),
            new FreebiePointsData("9", "Bolsa de Tela (Tote Bag)", "Bolsa reutilizable con diseño minimalista", 100, 10, 100),
            new FreebiePointsData("10", "Cuaderno de Post-its", "Set de notas adhesivas personalizadas", 100, 8, 100)
    );

    // ---------------- CONFERENCIAS + -----------------
    private static final List<ConferencePointsData> INVENTORY_DATA_CONFERENCIAS = List.of(
            new ConferencePointsData("1", "Lunes", "01-03", "09:00", "10:00", "1.0", "Sede Central - Auditorio Principal", "Conferencia Inaugural", "La IA como herramienta de diagnóstico psicológico", "Dr. Ana Pérez", 5),
            new ConferencePointsData("2", "Lunes", "01/03", "10:00", "11:30", "1.5", "Sede Central - Auditorio Principal", "Panel de Expertos", "Ética y privacidad en la terapia online", "Dra. Carlos Gómez,Mtro. Elena Ruiz", 3),
            new ConferencePointsData("3", "Lunes", "01/03", "11:30", "12:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("4", "Lunes", "01/03", "12:00", "14:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Realidad Virtual para el tratamiento de fobias", "Lic. Javier Salas", 3),
            new ConferencePointsData("5", "Lunes", "01/03", "12:00", "14:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Uso de Wearables en la monitorización del estrés", "Msc. Laura Vega", 2),
            new ConferencePointsData("6", "Lunes", "01/03", "14:00", "15:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData("7", "Lunes", "01/03", "15:00", "16:30", "1.5", "Sede Oeste - Sala de Conferencias", "Seminario", "Neurotecnología y mejora cognitiva", "Dr. Marco Polo", 3),
            new ConferencePointsData("8", "Martes", "02/03", "09:00", "11:00", "2.0", "Sede Central - Auditorio Principal", "Keynote Speaker", "Big Data en la investigación psicológica", "Dra. Sofía Torres", 3),
            new ConferencePointsData("9", "Martes", "02/03", "11:00", "11:30", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("10", "Martes", "02/03", "11:30", "13:30", "2.0", "Sede Norte - Aula Magna", "Taller", "Gamificación en intervenciones psicológicas con adolescentes", "Lic. Daniel Arias", 5),
            new ConferencePointsData("11", "Martes", "02/03", "11:30", "13:30", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Psicología del consumo en plataformas digitales", "Msc. Patricia Rey", 2),
            new ConferencePointsData("12", "Martes", "02/03", "13:30", "14:30", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData("13", "Martes", "02/03", "14:30", "16:30", "2.0", "Sede Oeste - Sala de Conferencias", "Mesa Redonda", "Ciberacoso: Impacto y Prevención Tecnológica", "Diversos Especialistas", 3),
            new ConferencePointsData("14", "Miércoles", "03/03", "09:00", "10:30", "1.5", "Sede Central - Auditorio Principal", "Conferencia", "El futuro de la salud mental digital", "Dr. Juan Blanco", 5),
            new ConferencePointsData("15", "Miércoles", "03/03", "10:30", "11:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("16", "Miércoles", "03/03", "11:00", "13:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Diseño de Chatbots Terapéuticos", "Ing. Miguel Ángel", 3),
            new ConferencePointsData("17", "Miércoles", "03/03", "11:00", "13:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Terapia Cognitivo-Conductual asistida por apps", "Msc. Carolina Solís", 2),
            new ConferencePointsData("18", "Miércoles", "03/03", "13:00", "14:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData("19", "Miércoles", "03/03", "14:00", "16:00", "2.0", "Sede Oeste - Sala de Conferencias", "Panel", "Regulación emocional a través de la Neurofeedback", "Dra. Fernanda Rivas", 5),
            new ConferencePointsData("20", "Jueves", "04/03", "09:00", "10:00", "1.0", "Sede Central - Auditorio Principal", "Conferencia", "Psicología Positiva y redes sociales", "Dr. Luis Herrera", 5),
            new ConferencePointsData("21", "Jueves", "04/03", "10:00", "11:30", "1.5", "Sede Central - Auditorio Principal", "Panel de Expertos", "Desafíos de la telepsicología en zonas rurales", "Diversos Especialistas", 3),
            new ConferencePointsData("22", "Jueves", "04/03", "11:30", "12:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("23", "Jueves", "04/03", "12:00", "14:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Machine Learning para la detección temprana de trastornos", "Ing. Valeria Soto", 3),
            new ConferencePointsData("24", "Jueves", "04/03", "12:00", "14:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Efectos psicológicos de la inmersión en mundos virtuales", "Msc. Ricardo Marín", 2),
            new ConferencePointsData("25", "Jueves", "04/03", "14:00", "15:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData("26", "Jueves", "04/03", "15:00", "16:30", "1.5", "Sede Oeste - Sala de Conferencias", "Seminario", "Diseño de interfaces inclusivas para la salud mental", "Dr. Jaime Pardo", 3),
            new ConferencePointsData("27", "Viernes", "05/03", "09:00", "11:00", "2.0", "Sede Central - Auditorio Principal", "Keynote Speaker", "La tecnología al servicio de la resiliencia", "Dra. Guadalupe Castro", 5),
            new ConferencePointsData("28", "Viernes", "05/03", "11:00", "11:30", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("29", "Viernes", "05/03", "11:30", "13:30", "2.0", "Sede Norte - Aula Magna", "Taller", "Uso de Biofeedback y BCI en psicología clínica", "Lic. Roberto León", 3),
            new ConferencePointsData("30", "Viernes", "05/03", "11:30", "13:30", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Impacto de los videojuegos en el desarrollo infantil", "Msc. Paula Gil", 2),
            new ConferencePointsData("31", "Viernes", "05/03", "13:30", "14:30", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData("32", "Viernes", "05/03", "14:30", "16:30", "2.0", "Sede Oeste - Sala de Conferencias", "Mesa Redonda", "Psicología del Trabajo en la era del teletrabajo y la robótica", "Diversos Especialistas", 5),
            new ConferencePointsData("33", "Sábado", "06/03", "09:00", "10:30", "1.5", "Sede Central - Auditorio Principal", "Conferencia", "El cerebro conectado: de la neurociencia a la tecnología", "Dr. Pedro Salas", 5),
            new ConferencePointsData("34", "Sábado", "06/03", "10:30", "11:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("35", "Sábado", "06/03", "11:00", "13:00", "2.0", "Sede Norte - Aula Magna", "Taller", "Introducción a la programación para psicólogos", "Ing. Andrea Soto", 5),
            new ConferencePointsData("36", "Sábado", "06/03", "11:00", "13:00", "2.0", "Sede Este - Sala 1", "Presentación de Trabajos", "Inteligencia Artificial en la educación emocional", "Msc. Felipe Neri", 2),
            new ConferencePointsData("37", "Sábado", "06/03", "13:00", "14:00", "1.0", "Todas las Sedes", "Almuerzo", "", "", 1),
            new ConferencePointsData("38", "Sábado", "06/03", "14:00", "16:00", "2.0", "Sede Oeste - Sala de Conferencias", "Panel", "Ciberseguridad y el bienestar psicológico", "Dra. Marta Vidal", 5),
            new ConferencePointsData("39", "Domingo", "07/03", "09:00", "10:00", "1.0", "Sede Central - Auditorio Principal", "Conferencia de Clausura", "Integrando mundos: El futuro de Psicología y Tecnología", "Dr. Antonio Ramos", 3),
            new ConferencePointsData("40", "Domingo", "07/03", "10:00", "11:30", "1.5", "Sede Central - Auditorio Principal", "Panel de Clausura", "Balance y Perspectivas del Congreso", "Comité Organizador", 5),
            new ConferencePointsData("41", "Domingo", "07/03", "11:30", "12:00", "0.5", "Todas las Sedes", "Receso", "Pausa Café", "", 1),
            new ConferencePointsData("42", "Domingo", "07/03", "12:00", "13:00", "1.0", "Sede Central - Auditorio Principal", "Acto de Clausura", "Entrega de Premios y Despedida", "Comité Organizador", 1),
            new ConferencePointsData("43", "Domingo", "07/03", "13:00", "14:00", "1.0", "Sede Oeste - Sala de Conferencias", "Networking", "Vino de honor / Cóctel", "", 1)
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