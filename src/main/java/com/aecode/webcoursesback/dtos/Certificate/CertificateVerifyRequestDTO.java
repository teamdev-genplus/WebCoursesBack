package com.aecode.webcoursesback.dtos.Certificate;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificateVerifyRequestDTO {
    private String code; // código ingresado por el usuario
}