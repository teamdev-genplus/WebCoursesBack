package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyCertificateDTO {
    private String certificateName;
    private String certificateUrl;
    // Nuevo: estado del certificado
    private boolean achieved;
}
