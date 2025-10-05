package com.proyecto.congreso.pases.controller;

import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.service.CertificateService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class CertificateControllerTest {


    private static final String BASE_URL = "/api/constancias";
    private static final Long CERTIFICATE_ID_1 = 1L;
    private static final Long CERTIFICATE_ID_2 = 2L;

    @Autowired
    private MockMvc mockMvc;

    // Usamos @MockBean para el servicio del que depende el controlador
    @MockitoBean
    private CertificateService certificateService;

    private Certificate testCertificate1;
    private Certificate testCertificate2;

    @BeforeEach
    void setUp() {
        // Configuración de la primera entidad Certificate
        testCertificate1 = new Certificate();
        testCertificate1.setCertificateId(CERTIFICATE_ID_1);
        testCertificate1.setParticipantId(100L);

        // Configuración de la segunda entidad Certificate
        testCertificate2 = new Certificate();
        testCertificate2.setCertificateId(CERTIFICATE_ID_2);
        testCertificate2.setParticipantId(101L);
    }

    // -------------------------------------------------------------------------
    // GET /api/constancias/certificados
    // -------------------------------------------------------------------------

    @Test
    void shouldGetAllCertificatesSuccessfully() throws Exception {
        // Given
        List<Certificate> certificates = Arrays.asList(testCertificate1, testCertificate2);

        // Cuando se llama al servicio, devuelve nuestra lista mockeada
        when(certificateService.getAllCertificate()).thenReturn(certificates);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/certificados")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // Esperamos un array JSON
                .andExpect(jsonPath("$.length()").value(2)) // Esperamos 2 elementos

                // Verificamos los campos del primer elemento (asumiendo que CertificateResponse usa 'certificateId' o similar)
                .andExpect(jsonPath("$[0].certificateId").value(CERTIFICATE_ID_1));

        // Verificamos que el servicio haya sido llamado exactamente una vez
        verify(certificateService).getAllCertificate();
    }
}
