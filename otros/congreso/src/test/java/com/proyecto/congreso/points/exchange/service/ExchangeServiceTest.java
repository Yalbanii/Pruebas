package com.proyecto.congreso.points.exchange.service;

import com.proyecto.congreso.points.calculator.model.Freebies;
import com.proyecto.congreso.points.calculator.repository.FreebieRepository;
import com.proyecto.congreso.points.exchange.dto.ExchangeResponse;
import com.proyecto.congreso.points.exchange.events.ExchangeRegisteredEvent;
import com.proyecto.congreso.points.exchange.model.Exchange;
import com.proyecto.congreso.points.exchange.repository.ExchangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {
    private static final Long PASS_ID = 101L;
    private static final Long PARTICIPANT_ID = 900L;
    private static final String FREEBIE_ID = "F001";
    private static final String EXCHANGE_ID = "EX-MONGO-123";
    private static final Integer COSTO = 15;
    private static final String ARTICULO = "Llavero 3D (Cerebro)";

    @Mock
    private ExchangeRepository exchangeRepository;
    @Mock
    private FreebieRepository freebieRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    // Spy se usa para mockear el método privado getParticipantIdFromPass
    @Spy
    @InjectMocks
    private ExchangeService exchangeService;

    private Freebies mockFreebie;
    private Exchange mockExchangeSaved;
    private List<Exchange> mockExchangeList;

    @BeforeEach
    void setUp() {
        // Mock de la entidad Freebies
        mockFreebie = new Freebies();
        mockFreebie.setFreebieId(FREEBIE_ID);
        mockFreebie.setArticulo(ARTICULO);
        mockFreebie.setCosto(COSTO);

        // Mock de la entidad Exchange tal como se guardaría
        mockExchangeSaved = new Exchange();
        mockExchangeSaved.setId(EXCHANGE_ID);
        mockExchangeSaved.setPassId(PASS_ID);
        mockExchangeSaved.setParticipantId(PARTICIPANT_ID);
        mockExchangeSaved.setFreebieId(FREEBIE_ID);
        mockExchangeSaved.setArticulo(ARTICULO);
        mockExchangeSaved.setPuntosReducidos(COSTO);
        mockExchangeSaved.setFechaIntercambio(LocalDateTime.now()); // Necesario para evitar NullPointerException en DTO

        // Mock de una lista de Exchanges
        mockExchangeList = List.of(mockExchangeSaved);
    }

    // -------------------------------------------------------------------------
    // Test: crearExchange
    // -------------------------------------------------------------------------

    @Test
    void crearExchange_shouldSuccessfullyRegisterAndPublishEvent() {
        // Given
        // 1. Simular Freebie existe
        when(freebieRepository.findById(FREEBIE_ID)).thenReturn(Optional.of(mockFreebie));
        // 2. Mockear el método REST simulado (usando doReturn en el spy)
        doReturn(PARTICIPANT_ID).when(exchangeService).getParticipantIdFromPass(PASS_ID);
        // 3. Simular guardado de Exchange
        when(exchangeRepository.save(any(Exchange.class))).thenReturn(mockExchangeSaved);

        // When
        ExchangeResponse response = exchangeService.crearExchange(PASS_ID, FREEBIE_ID);

        // Then
        // 1. Verificar llamadas a repositorios
        verify(freebieRepository).findById(FREEBIE_ID);
        verify(exchangeRepository).save(any(Exchange.class));

        // 2. Verificar que el evento fue publicado y capturar su contenido
        ArgumentCaptor<ExchangeRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(ExchangeRegisteredEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        // 3. Verificar el contenido del evento
        ExchangeRegisteredEvent publishedEvent = eventCaptor.getValue();
        assertEquals(PASS_ID, publishedEvent.getPassId());
        assertEquals(FREEBIE_ID, publishedEvent.getFreebieId());
        assertEquals(COSTO, publishedEvent.getCosto());

        // 4. Verificar la respuesta del DTO
        assertNotNull(response);
        assertEquals(EXCHANGE_ID, response.getId());
        assertEquals(PARTICIPANT_ID, response.getParticipantId());
        assertEquals(COSTO, response.getCosto());
    }

    @Test
    void crearExchange_shouldThrowExceptionIfFreebieNotFound() {
        // Given
        // Simular que el Freebie NO existe
        when(freebieRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.crearExchange(PASS_ID, FREEBIE_ID);
        });

        assertTrue(exception.getMessage().contains("Freebie no encontrado"));
        verify(exchangeRepository, never()).save(any(Exchange.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    // Nota: El test para la falla de getParticipantIdFromPass requeriría simular la excepción
    // lanzada por el método privado, lo cual se hace cambiando el doReturn a doThrow.

    // -------------------------------------------------------------------------
    // Test: getExchangesByPass
    // -------------------------------------------------------------------------

    @Test
    void getExchangesByPass_shouldReturnListOfResponses() {
        // Given
        // Simular que el repositorio devuelve una lista
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
    void getExchangesByPass_shouldReturnEmptyListIfNotFound() {
        // Given
        when(exchangeRepository.findByPassId(PASS_ID)).thenReturn(Collections.emptyList());

        // When
        List<ExchangeResponse> responses = exchangeService.getExchangesByPass(PASS_ID);

        // Then
        assertTrue(responses.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Test: getTotalPuntosReducidos
    // -------------------------------------------------------------------------

    @Test
    void getTotalPuntosReducidos_shouldCalculateTotalPuntosCorrectly() {
        // Given
        Exchange exchange1 = mockExchangeSaved; // 15
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
    void getTotalPuntosReducidos_shouldReturnZeroIfNoExchangesFound() {
        // Given
        when(exchangeRepository.findExchangesProcedosByPass(PASS_ID)).thenReturn(Collections.emptyList());

        // When
        Integer total = exchangeService.getTotalPuntosReducidos(PASS_ID);

        // Then
        assertEquals(0, total);
    }

    // -------------------------------------------------------------------------
    // Test: countExchangesByPass
    // -------------------------------------------------------------------------

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