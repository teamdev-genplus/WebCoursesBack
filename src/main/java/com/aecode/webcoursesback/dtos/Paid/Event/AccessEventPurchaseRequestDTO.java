package com.aecode.webcoursesback.dtos.Paid.Event;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessEventPurchaseRequestDTO {

    @NotBlank
    private String clerkId;

    @NotBlank
    private String purchaseNumber;            // único (idempotente)

    private OffsetDateTime purchaseAt;        // opcional; si null => now
    private String purchaseDateLabel;         // opcional

    // ==== EVENT ====
    @NotBlank
    private String planKey;                   // ej. "aecoder" | "corporativo" | "general"

    @Min(1)
    private Integer quantity;                 // default 1 si null

    private String couponCode;                // opcional (solo referencia)

    // ==== TOTALES ENVIADOS POR EL FRONT ====
    @NotNull @Digits(integer = 12, fraction = 2)
    private BigDecimal total;

    @Digits(integer = 12, fraction = 2)
    private BigDecimal discount;

    @Digits(integer = 12, fraction = 2)
    private BigDecimal commission;

    @Digits(integer = 12, fraction = 2)
    private BigDecimal subtotal;              // si null, se intenta calcular con investment

    @NotNull
    private PaymentMethod method;             // PAYPAL, YAPE, PLIN, TRANSFER, IZIPAY

    @NotNull
    private CurrencyCode currency;            // PEN, USD

    public enum PaymentMethod { PAYPAL, YAPE, PLIN, TRANSFER }
    public enum CurrencyCode { PEN, USD }
}