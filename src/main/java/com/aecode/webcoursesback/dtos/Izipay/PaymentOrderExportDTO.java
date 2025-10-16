package com.aecode.webcoursesback.dtos.Izipay;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentOrderExportDTO {
    // Datos generales
    private String orderId;
    private String domain;   // "MODULES" | "EVENT"
    private String status;   // "PENDING" | "PAID" | "UNPAID" | "FAILED"
    private String mode;     // "TEST" | "PROD" (si aplica)

    private Integer amountCents;
    private String currency;

    private String clerkId;
    private String email;

    private boolean entitlementsGranted;
    private OffsetDateTime grantedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Solo EVENT
    private String  landingSlug;
    private String  landingPlanKey;
    private Integer landingQuantity;
    private String  landingCouponCode;

    // Solo MODULES (lista de módulos de la orden)
    private List<Long> moduleIds;
}
