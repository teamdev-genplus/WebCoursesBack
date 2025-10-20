package com.aecode.webcoursesback.dtos.Business;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessDemoRequestDTO {
    private Long id;
    private String companyName;
    private String fullName;
    private String email;
    private String interestLine;
    private String countryCode;
    private String phone;
    private String message;
    private String status;       // "PENDING" | "CONTACTED"
    private String createdAt;    // ISO string
}
