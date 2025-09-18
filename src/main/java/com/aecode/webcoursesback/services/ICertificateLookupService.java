package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Certificate.CertificateDetailDTO;
import com.aecode.webcoursesback.dtos.Certificate.CertificateVerifyResponseDTO;

public interface ICertificateLookupService {
    CertificateVerifyResponseDTO verify(String code);
    CertificateDetailDTO getDetails(String code);
}
