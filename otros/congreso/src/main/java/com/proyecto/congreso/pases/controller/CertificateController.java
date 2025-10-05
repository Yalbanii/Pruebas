package com.proyecto.congreso.pases.controller;

import com.proyecto.congreso.pases.dto.CertificateResponse;
import com.proyecto.congreso.pases.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/constancias")
@RequiredArgsConstructor
@Tag(name = "Certificados", description = "Review of Certificate Reached APIs")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/certificados")
    @Operation(summary = "Get all Certificates")
    public ResponseEntity<List<CertificateResponse>> getAllCertificate() {
        List<CertificateResponse> certificate = certificateService.getAllCertificate()
                .stream()
                .map(CertificateResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(certificate);
    }

}

