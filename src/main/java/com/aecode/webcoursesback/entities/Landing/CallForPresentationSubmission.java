package com.aecode.webcoursesback.entities.Landing;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "call_for_presentation_submissions")
public class CallForPresentationSubmission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Para saber a qué landing pertenece la solicitud */
    @Column( length = 120)
    private String landingSlug;

    /** Datos del solicitante */
    @Column( length = 140)
    private String fullName;

    @Column(length = 180)
    private String email;

    /** Código de país como se ve en el front: "+51" (no normalizamos aquí) */
    @Column( length = 6)
    private String countryCode;

    /** Número local sin el código (solo dígitos) */
    @Column( length = 30)
    private String phoneNumber;

    /** Descripción de la idea/propuesta */
    @Column(columnDefinition = "TEXT")
    private String ideaText;

    /** Opcional si el usuario estaba logueado */
    private String clerkId;

    /** Estado: PENDIENTE / REVISADA */
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Status status;

    public enum Status { PENDING, REVIEWED }

    private OffsetDateTime createdAt;
    private OffsetDateTime reviewedAt; // null mientras esté pendiente

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = Status.PENDING;
    }
}
