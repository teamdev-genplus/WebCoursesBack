package com.aecode.webcoursesback.dtos.Landing.Solicitud;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CallForPresentationSubmissionDTO {
    private Long id;
    private String landingSlug;
    private String fullName;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String ideaText;
    private String status;       // "PENDING" | "REVIEWED"
    private String createdAt;    // ISO string
}