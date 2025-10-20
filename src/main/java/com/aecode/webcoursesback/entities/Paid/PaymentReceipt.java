package com.aecode.webcoursesback.entities.Paid;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

@Entity
@Table(name = "payment_receipts", uniqueConstraints = {
        @UniqueConstraint(name = "uq_payment_receipts_purchase_number", columnNames = {"purchase_number"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentReceipt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_number", nullable = false, length = 64)
    private String purchaseNumber;

    @Column(name = "clerk_id", nullable = false, length = 128)
    private String clerkId;

    @Column(name = "user_fullname", length = 256)
    private String userFullName;

    @Column(name = "user_email", length = 256)
    private String userEmail;

    @Column(name = "purchase_at", nullable = false)
    private OffsetDateTime purchaseAt;

    @Column(name = "purchase_date_label", length = 128)
    private String purchaseDateLabel; // "22 de Agosto del 2025" si front lo manda

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 32)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 8)
    private CurrencyCode currency;

    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount", precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(name = "commission", precision = 12, scale = 2)
    private BigDecimal commission;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    // ====== PARA MODULES (se conserva para reporteo) ======
    @Column(name = "module_ids_csv", length = 1024)
    private String moduleIdsCsv;

    // ====== NUEVO: SOPORTE DE DOMINIO ======
    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false, length = 16)
    private PaymentDomain domain; // MODULES | EVENT

    // ====== NUEVO: CAMPOS EVENT ======
    @Column(name = "event_slug", length = 200)
    private String eventSlug;       // landing slug

    @Column(name = "event_plan_key", length = 80)
    private String eventPlanKey;    // key del plan

    @Column(name = "event_quantity")
    private Integer eventQuantity;  // cantidad de boletos

    @Column(name = "event_coupon_code", length = 60)
    private String eventCouponCode; // opcional (solo para referencia)


    @Column(name = "entitlements_granted", nullable = false)
    private boolean entitlementsGranted;

    @Column(name = "granted_at")
    private OffsetDateTime grantedAt;

    public enum PaymentMethod { PAYPAL, YAPE, PLIN, TRANSFER }
    public enum CurrencyCode { PEN, USD }
    public enum PaymentDomain { MODULES, EVENT }
}
