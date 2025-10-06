package com.proyecto.congreso.points.exchange.service;

import com.proyecto.congreso.pases.model.Pass;
import com.proyecto.congreso.pases.repository.PassRepository;
import com.proyecto.congreso.points.calculator.model.Freebies;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.exchange.dto.ExchangeResponse;
import com.proyecto.congreso.points.exchange.events.ExchangeFailedEvent;
import com.proyecto.congreso.points.exchange.events.ExchangeRegisteredEvent;
import com.proyecto.congreso.points.exchange.model.Exchange;
import com.proyecto.congreso.points.exchange.repository.ExchangeRepository;
import com.proyecto.congreso.points.service.FreebieStockHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {

    private static final Long PASS_ID = 101L;
    private static final Long PARTICIPANT_ID = 900L;
    private static final String FREEBIE_ID = "F001";
    private static final String EXCHANGE_ID = "EX-MONGO-123";
    private static final Integer COSTO = 15;
    private static final String ARTICULO = "Llavero 3D (Cerebro)";
    private static final Integer BALANCE_SUFICIENTE = 50;

    @Mock
    private ExchangeRepository exchangeRepository;
    @Mock
    private FreebieRepository freebieRepository;
    @Mock
    private PassRepository passRepository;
    @Mock
    private FreebieStockHandler stockHandler;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ExchangeService exchangeService;

    private Pass mockPass;
    private Freebies mockFreebie;
    private Exchange mockExchangeSaved;
    private List<Exchange> mockExchangeList;

    @BeforeEach
    void setUp() {
        // Pass Mock (Estado ACTIVO y saldo suficiente)
        mockPass = new Pass();
        mockPass.setPassId(PASS_ID);
        mockPass.setParticipantId(PARTICIPANT_ID);
        mockPass.setStatus(Pass.PassStatus.ACTIVE);
        mockPass.setPointsBalance(BALANCE_SUFICIENTE);

        // Freebie Mock (Con stock)
        mockFreebie = new Freebies();
        mockFreebie.setFreebieId(FREEBIE_ID);
        mockFreebie.setArticulo(ARTICULO);
        mockFreebie.setCosto(COSTO);
        mockFreebie.setStockActual(10); // Stock suficiente

        // Exchange Mock (Resultado después de guardar)
        mockExchangeSaved = new Exchange();
        mockExchangeSaved.setId(EXCHANGE_ID);
        mockExchangeSaved.setPassId(PASS_ID);
        mockExchangeSaved.setParticipantId(PARTICIPANT_ID);
        mockExchangeSaved.setFreebieId(FREEBIE_ID);
        mockExchangeSaved.setArticulo(ARTICULO);
        mockExchangeSaved.setPuntosReducidos(COSTO);
        mockExchangeSaved.setFechaIntercambio(LocalDateTime.now());

        // Lista para pruebas de consulta
        mockExchangeList = List.of(mockExchangeSaved);
    }

// -------------------------------------------------------------------------
// ## Test: crearExchange (Flujo de Éxito y Fallas)
// -------------------------------------------------------------------------

    @Test
    void crearExchange_shouldSuccessfullyCompleteTransaction() {
        // Given
        // 1. Validaciones
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(mockPass));
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.of(mockFreebie));

        // 2. Reducir Stock (Éxito)
        when(stockHandler.reduceStock(FREEBIE_ID)).thenReturn(true);

        // 3. Crear Registro
        when(exchangeRepository.save(any(Exchange.class))).thenReturn(mockExchangeSaved);

        // When
        ExchangeResponse response = exchangeService.crearExchange(PASS_ID, FREEBIE_ID);

        // Then
        // 1. Verificaciones de llamadas clave
        verify(stockHandler).reduceStock(FREEBIE_ID);
        verify(exchangeRepository).save(any(Exchange.class));
        verify(eventPublisher).publishEvent(any(ExchangeRegisteredEvent.class));
        verify(eventPublisher, never()).publishEvent(any(ExchangeFailedEvent.class)); // No debe publicar evento de fallo

        // 2. Verificación de la respuesta
        assertNotNull(response);
        assertEquals(EXCHANGE_ID, response.getId());
        assertEquals(COSTO, response.getCosto());
    }

    // --- Escenarios de Falla (Validaciones) ---

    @Test
    void crearExchange_shouldThrowException_whenPassNotFound() {
        // Given
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.crearExchange(PASS_ID, FREEBIE_ID);
        });

        // Asegurar que no se tocó el stock ni se guardó el registro
        verify(freebieRepository, never()).findById(any());
        verify(stockHandler, never()).reduceStock(any());
        verify(exchangeRepository, never()).save(any());
    }


    @Test
    void crearExchange_shouldThrowException_whenFreebieNotFound() {
        // Given
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(mockPass));
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.crearExchange(PASS_ID, FREEBIE_ID);
        });

        verify(stockHandler, never()).reduceStock(any());
        verify(exchangeRepository, never()).save(any());
    }

    @Test
    void crearExchange_shouldThrowException_whenStockIsZero() {
        // Given
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(mockPass));
        mockFreebie.setStockActual(0); // Setear stock en cero
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.of(mockFreebie));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.crearExchange(PASS_ID, FREEBIE_ID);
        }, "Debe fallar por stock insuficiente.");

        verify(stockHandler, never()).reduceStock(any());
    }

    @Test
    void crearExchange_shouldThrowException_whenInsufficientPoints() {
        // Given
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(mockPass));
        mockPass.setPointsBalance(COSTO - 1); // Setear saldo insuficiente
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.of(mockFreebie));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.crearExchange(PASS_ID, FREEBIE_ID);
        }, "Debe fallar por puntos insuficientes.");

        verify(stockHandler, never()).reduceStock(any());
    }

    // --- Escenario de Falla (Después de Validaciones) ---

    @Test
    void crearExchange_shouldThrowException_whenStockHandlerFails() {
        // Given
        // Validaciones exitosas
        when(passRepository.findById(PASS_ID)).thenReturn(Optional.of(mockPass));
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.of(mockFreebie));

        // Falla al reducir stock
        when(stockHandler.reduceStock(FREEBIE_ID)).thenReturn(false);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            exchangeService.crearExchange(PASS_ID, FREEBIE_ID);
        });

        verify(exchangeRepository, never()).save(any()); // No debe guardar
        verify(eventPublisher, never()).publishEvent(any(ExchangeRegisteredEvent.class));
        verify(eventPublisher, never()).publishEvent(any(ExchangeFailedEvent.class)); // No se revierte si falla la reducción inicial.
    }


// -------------------------------------------------------------------------
// ## Test: Métodos de Consulta (readOnly)
// -------------------------------------------------------------------------

    @Test
    void getExchangesByPass_shouldReturnListOfResponses() {
        // Given
        when(exchangeRepository.findByPassId(PASS_ID)).thenReturn(mockExchangeList);

        // When
        List<ExchangeResponse> responses = exchangeService.getExchangesByPass(PASS_ID);

        // Then
        verify(exchangeRepository).findByPassId(PASS_ID);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(EXCHANGE_ID, responses.get(0).getId());
    }

    @Test
    void getTotalPuntosReducidos_shouldCalculateTotalPuntosCorrectly() {
        // Given
        Exchange exchange1 = mockExchangeSaved; // 15 puntos
        Exchange exchange2 = new Exchange();
        exchange2.setPuntosReducidos(25);

        List<Exchange> exchanges = List.of(exchange1, exchange2);
        when(exchangeRepository.findExchangesProcedosByPass(PASS_ID)).thenReturn(exchanges);

        // When
        Integer total = exchangeService.getTotalPuntosReducidos(PASS_ID);

        // Then
        assertEquals(40, total); // 15 + 25
    }

    @Test
    void countExchangesByPass_shouldReturnCorrectCount() {
        // Given
        long expectedCount = 5L;
        when(exchangeRepository.countByPassId(PASS_ID)).thenReturn(expectedCount);

        // When
        long count = exchangeService.countExchangesByPass(PASS_ID);

        // Then
        assertEquals(expectedCount, count);
    }
}