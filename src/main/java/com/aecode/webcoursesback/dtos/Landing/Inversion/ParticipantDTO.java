package com.aecode.webcoursesback.dtos.Landing.Inversion;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParticipantDTO {
    private Long id;
    private String landingSlug;
    private String modality;       // PRESENCIAL | VIRTUAL
    private String planKey;        // general | aecoder | corporativo
    private String buyerClerkId;
    private String groupId;        // agrupa la compra previa al pago
    private Integer participantIndex;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String documentType;
    private String documentNumber;
    private String company;

    private String status;         // PENDING | CONFIRMED | CANCELED
    private String orderReference; // opcional (rellenable post-pago)
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
