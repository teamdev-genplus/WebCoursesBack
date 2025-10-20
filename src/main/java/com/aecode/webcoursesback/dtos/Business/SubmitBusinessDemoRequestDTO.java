package com.aecode.webcoursesback.dtos.Business;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitBusinessDemoRequestDTO {

    @NotBlank
    private String companyName;

    @NotBlank
    private String fullName;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String interestLine;

    @NotBlank
    private String countryCode; // "+51"

    @NotBlank
    private String phone;       // nacional

    private String message;     // opcional
    private String clerkId;     // opcional
}
