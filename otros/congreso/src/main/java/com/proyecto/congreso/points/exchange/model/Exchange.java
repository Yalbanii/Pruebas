package com.proyecto.congreso.points.exchange.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "intercambios")
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Long passId;
    private Long participantId;
    private String freebieId;
    private String articulo;
    private Integer puntosReducidos;
    private LocalDateTime fechaIntercambio;

    public static Exchange crear(Long passId, Long participantId, String freebieId,
                                   String articulo, Integer puntosReducidos) {
        Exchange exchange = new Exchange();
        exchange.setPassId(passId);
        exchange.setParticipantId(participantId);
        exchange.setFreebieId(freebieId);
        exchange.setArticulo(articulo);
        exchange.setPuntosReducidos(puntosReducidos);
        exchange.setFechaIntercambio(LocalDateTime.now());
        return exchange;
    }
}
