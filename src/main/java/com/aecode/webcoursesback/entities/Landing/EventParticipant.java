package com.aecode.webcoursesback.entities.Landing;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "event_participants", indexes = {
        @Index(name = "ix_evpart_slug", columnList = "landing_slug"),
        @Index(name = "ix_evpart_clerk", columnList = "buyer_clerk_id"),
        @Index(name = "ix_evpart_group", columnList = "group_id"),
        @Index(name = "ix_evpart_status", columnList = "status")
})
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifica la landing (ej: "ai-construction-summit-2025") */
    @Column(name = "landing_slug", nullable = false, length = 150)
    private String landingSlug;

    /** Modalidad: PRESENCIAL | VIRTUAL (normalizada en servicio) */
    @Column(name = "modality", length = 20, nullable = false)
    private String modality;

    /** Plan key: "general" | "aecoder" | "corporativo" (o lo que definas) */
    @Column(name = "plan_key", length = 80, nullable = false)
    private String planKey;

    /** ClerkId del comprador (siempre requerido) */
    @Column(name = "buyer_clerk_id", length = 120, nullable = false)
    private String buyerClerkId;

    /** Agrupador lógico previo al pago (UUID generado si no llega) */
    @Column(name = "group_id", length = 60, nullable = false)
    private String groupId;

    /** Índice del participante en la compra (1..N) — sugerido para orden */
    @Column(name = "participant_index")
    private Integer participantIndex;

    /** Datos del participante */
    @Column(name = "full_name", length = 120, nullable = false)
    private String fullname;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "phone", length = 60, nullable = false)
    private String phone;

    @Column(name = "document_type", length = 40, nullable = false)
    private String documentType;

    @Column(name = "document_number", length = 60, nullable = false)
    private String documentNumber;

    @Column(name = "company", length = 200)
    private String company;

    @Column(name = "rol", length = 200)
    private String rol;

    @Column(name = "linkedin", length = 200)
    private String linkedin;

    /** Estado: PENDING (por defecto) | CONFIRMED | CANCELED */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.PENDING;

    /** Si luego quieres enlazar con un comprobante/orden, queda opcional */
    @Column(name = "order_reference", length = 120)
    private String orderReference; // puede ser purchaseNumber, orderId, etc (opcional)

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public enum Status { PENDING, CONFIRMED, CANCELED }

    @PrePersist
    public void prePersist() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = Status.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}


