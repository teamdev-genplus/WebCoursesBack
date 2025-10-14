package com.aecode.webcoursesback.dtos.Landing.Solicitud;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitCallForPresentationDTO {
    private String fullName;
    private String email;
    private String countryCode;  // Ej: "+51"
    private String phoneNumber;  // Solo dígitos sin el +51
    private String ideaText;
    private String clerkId;      // opcional
}