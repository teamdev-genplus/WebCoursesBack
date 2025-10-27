package com.aecode.webcoursesback.entities.Izipay;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "payment_orders", indexes = {
        @Index(name = "ux_payment_orders_order_id", columnList = "order_id", unique = true)
})
@SequenceGenerator(name = "pay_order_seq", sequenceName = "payment_order_seq", allocationSize = 1)
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_order_seq")
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, length = 120)
    private String orderId;            // el que mandas a Izipay

    @Column(name = "clerk_id", length = 120)
    private String clerkId;            // para enlazar con tu UserProfile

    @Column(name = "email", length = 255)
    private String email;              // redundante (de UserProfile o de request)

    @Column(name = "amount_cents")
    private Integer amountCents;       // monto en centavos (PEN)

    @Column(name = "currency", length = 10)
    private String currency;           // "PEN" | "USD"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status;      // PENDING | PAID | UNPAID | FAILED

    @Column(name = "form_token", columnDefinition = "TEXT")
    private String formToken;          // último formToken generado

    @Column(name = "mode", length = 20)
    private String mode;               // "TEST" o "PROD" (opcional, informativo)

    // ===== NUEVO: idempotencia de concesión =====
    @Column(name = "entitlements_granted")
    private boolean entitlementsGranted = false;

    @Column(name = "granted_at")
    private OffsetDateTime grantedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public enum PaymentStatus {
        PENDING, PAID, UNPAID, FAILED
    }

    //NUevo
    public enum OrderDomain { MODULES, EVENT }
    // ===== NUEVO: dominio e info de evento =====
    @Enumerated(EnumType.STRING)
    @Column(name = "domain", length = 20)
    private OrderDomain domain = OrderDomain.MODULES;

    @Column(name = "landing_slug", length = 150)
    private String landingSlug;        // ej: "ai-construction-summit-2025"

    @Column(name = "landing_modality", length = 20)
    private String landingModality; // "PRESENCIAL" | "VIRTUAL"

    @Column(name = "landing_plan_key", length = 80)
    private String landingPlanKey;     // ej: "regular" | "comunidad" | "corporativo"
    // NUEVOS (ambos opcionales)
    @Column
    private Integer landingQuantity;     // null => se usa default por plan

    @Column(length = 60)
    private String landingCouponCode;    // null => sin cupón

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = PaymentStatus.PENDING;
        if (domain == null) domain = OrderDomain.MODULES;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}