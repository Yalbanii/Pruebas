package com.proyecto.congreso.pases.service;

import com.proyecto.congreso.pases.model.Certificate;
import com.proyecto.congreso.pases.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Certificate> getAllCertificate() {
        log.debug("Getting all Pass");
        return certificateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificate> findByPassId(Long id) {
        log.debug("Getting Certificate by Pass id: {}", id);
        return Collections.singletonList(certificateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pass not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPassId(Long passId) {
        return certificateRepository.existsByPassId(passId);
    }

}
