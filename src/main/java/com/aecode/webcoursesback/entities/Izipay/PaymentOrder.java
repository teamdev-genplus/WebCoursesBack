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

    @Column(name = "amount_cents", nullable = false)
    private Integer amountCents;       // monto en centavos (PEN)

    @Column(name = "currency", length = 10, nullable = false)
    private String currency;           // "PEN" | "USD"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PaymentStatus status;      // PENDING | PAID | UNPAID | FAILED

    @Column(name = "form_token", length = 2000)
    private String formToken;          // último formToken generado

    @Column(name = "mode", length = 20)
    private String mode;               // "TEST" o "PROD" (opcional, informativo)

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public enum PaymentStatus {
        PENDING, PAID, UNPAID, FAILED
    }

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = PaymentStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}