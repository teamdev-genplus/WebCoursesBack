package com.aecode.webcoursesback.dtos.Certificate;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificateVerifyResponseDTO {
    private boolean verified;              // true si existe
    private String message;                // "Certificado verificado exitosamente"
    private String certificateCode;        // eco del código
    private String studentFullName;        // nombre del alumno
    private String courseOrModuleTitle;    // título mostrado en la vista (course.title o module.titleStudyplan)
    private LocalDate issuedAt;            // fecha de emisión
    private Integer durationHours;         // duración (curso o módulo)
}