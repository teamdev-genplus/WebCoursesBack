package com.aecode.webcoursesback.dtos.Paid.Voucher;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

/** Parte JSON del multipart (request part "payload") */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManualPaymentVoucherPayload {

    // ===== Identidad / compra =====
    private String clerkId;                 // recomendado si quieres resolver email
    private String paymentMethod;           // texto libre: TRANSFER, YAPE, PLIN, etc.
    private OffsetDateTime paidAt;          // opcional
    private String status;                  // "PENDING" | "PAID" (default PAID en entidad)

    // ===== Campos de módulos (solo si domain = MODULES) =====
    private List<Long> moduleIds;           // opcional, puede ser vacío o null

    // ===== Nuevos campos generales =====
    private String email;                   // se ignora si clerkId resuelve a un usuario
    private String orderId;                 // nro/orden de pago
    private Integer amountCents;            // total en centavos
    private String currency;                // "PEN" | "USD" (opcional)
    private String domain;                  // "MODULES" | "EVENT" (opcional, null -> MODULES implícito)

    // ===== Campos EVENT =====
    private String landingSlug;             // ej: "ai-construction-summit-2025"
    private String landingPlanKey;          // ej: "regular", "comunidad", "corporativo"
    private Integer landingQuantity;        // ej: 1, 2, 10
    private String landingModality;        // ej: PRESENCIAL, VIRTUAL
}
