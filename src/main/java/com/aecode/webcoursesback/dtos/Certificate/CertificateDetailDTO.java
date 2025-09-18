package com.aecode.webcoursesback.dtos.Certificate;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificateDetailDTO {
    private String certificateCode;
    private String studentFullName;

    private String entityType;             // "COURSE" o "MODULE"
    private Long courseId;                 // cuando aplique
    private Long moduleId;                 // cuando aplique

    private String title;                  // Course.title o Module.titleStudyplan
    private String descriptionByCertificate; // Course.descriptionbyCertificate o Module.descriptionbyCertificate

    private LocalDate issuedAt;

    private String certificateImage;       // preview grande
    private String certificateUrl;         // enlace de descarga
}