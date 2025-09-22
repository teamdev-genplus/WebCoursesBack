package com.aecode.webcoursesback.entities.Paid;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "unified_paid_orders")
@SequenceGenerator(name = "unified_paid_orders_seq", sequenceName = "unified_paid_orders_seq", allocationSize = 1)
public class UnifiedPaidOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unified_paid_orders_seq")
    private Long id;

    /** CORREO */
    @Column(length = 255, nullable = false)
    private String email;

    /** NOMBRE */
    @Column(length = 255, nullable = false)
    private String fullName;

    /** ESTADO del pago — normalmente PAID */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status;

    /** FECHA DEL PAGO */
    @Column(nullable = false)
    private OffsetDateTime paidAt;

    /** LISTA DE MÓDULOS (IDs) en CSV, p.ej. "12,15,20" */
    @Column(name = "module_ids_csv", columnDefinition = "TEXT", nullable = false)
    private String moduleIdsCsv;

    public enum PaymentStatus { PAID, REFUNDED, CHARGEBACK }
}
