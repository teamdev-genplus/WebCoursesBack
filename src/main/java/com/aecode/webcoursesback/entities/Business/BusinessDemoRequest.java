package com.aecode.webcoursesback.entities.Business;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "business_demo_requests")
public class BusinessDemoRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Datos del formulario (según tu front) */
    @Column(length = 160, nullable = false)
    private String companyName;

    @Column(length = 140, nullable = false)
    private String fullName;

    @Column(length = 180, nullable = false)
    private String email;

    @Column(length = 80, nullable = false)
    private String interestLine; // "Consultoría & PMO", "BIM para Proyectos", etc.

    /* Teléfono partido como en el front */
    @Column(length = 6, nullable = false)
    private String countryCode;  // ej. "+51"

    @Column(length = 30, nullable = false)
    private String phone;        // número nacional

    @Column(columnDefinition = "TEXT")
    private String message;      // opcional

    /* Opcional si tienes auth */
    private String clerkId;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private Status status;       // PENDING | REVIEWED

    public enum Status { PENDING, REVIEWED }

    private OffsetDateTime createdAt;
    private OffsetDateTime processedAt; // cuando el equipo contacte/gestione

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = Status.PENDING;
    }
}
