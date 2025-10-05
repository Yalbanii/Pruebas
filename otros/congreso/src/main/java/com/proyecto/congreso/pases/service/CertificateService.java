package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Certificate;

import java.util.List;

public interface CertificateService {

    List<Certificate> getAllCertificate();
    boolean existsByPassId(Long passId);

}
