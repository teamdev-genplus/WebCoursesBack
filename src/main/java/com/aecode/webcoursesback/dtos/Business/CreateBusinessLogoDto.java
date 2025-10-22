package com.aecode.webcoursesback.dtos.Business;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateBusinessLogoDto {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El logoUrl es requerido")
    private String logoUrl;
    private String websiteUrl;
}