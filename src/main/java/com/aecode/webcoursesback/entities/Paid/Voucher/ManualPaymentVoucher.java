package com.aecode.webcoursesback.entities.Paid.Voucher;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "manual_payment_vouchers")
@SequenceGenerator(name = "manual_voucher_seq", sequenceName = "manual_payment_voucher_seq", allocationSize = 1)
public class ManualPaymentVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_voucher_seq")
    private Long id;

    /** clerkId del usuario (debe existir para poder validar) */
    @Column(name = "clerk_id", length = 128)
    private String clerkId;

    /** URL pública del voucher subido a Firebase */
    @Column(name = "voucher_url", columnDefinition = "TEXT")
    private String voucherUrl;

    /** IDs de módulos en CSV (p.ej. "12,15,18") */
    @Column(name = "module_ids_csv", columnDefinition = "TEXT")
    private String moduleIdsCsv;

    /** Método de pago libre (TRANSFER, YAPE, PLIN, etc.) */
    @Column(name = "payment_method", length = 64)
    private String paymentMethod;

    /** Fecha del pago (opcional) */
    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    /** Estado del pago (PENDING | PAID) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16)
    private PaymentStatus status;

    /** Booleano de validación manual (false por defecto) */
    @Column(name = "validated")
    private boolean validated;

    /** Auditoría */
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public enum PaymentStatus { PENDING, PAID }

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        // Defaults según lo solicitado:
        if (status == null) status = PaymentStatus.PAID; // normalmente llega como pagado
        // Validated siempre inicia en false:
        validated = false;
        if (paidAt == null) paidAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}