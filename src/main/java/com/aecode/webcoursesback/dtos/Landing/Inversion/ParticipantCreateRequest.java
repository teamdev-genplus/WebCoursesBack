package com.aecode.webcoursesback.dtos.Landing.Inversion;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParticipantCreateRequest {
    private String modality;     // requerido
    private String planKey;      // requerido
    private String buyerClerkId; // requerido
    private String groupId;      // opcional (si no llega, se genera uno y se retorna)
    private Integer quantity;    // requerido para validar reglas mínimas/máximas del plan
    private Integer participantIndex; // requerido para orden (1..quantity)

    private String firstName;       // requerido
    private String lastName;        // requerido
    private String email;           // requerido
    private String phone;           // requerido
    private String documentType;    // requerido
    private String documentNumber;  // requerido
    private String company;         // opcional
}
