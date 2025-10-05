package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Certificate;

import java.util.List;

public interface CertificateService {

    List<Certificate> getAllCertificate();
    List<Certificate> findByPassId(Long passId);
    boolean existsByPassId(Long passId);

}
