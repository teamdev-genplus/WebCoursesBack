package com.aecode.webcoursesback.dtos.Paid.Event;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessEventPurchaseResponseDTO {
    private String purchaseNumber;
    private String clerkId;
    private String email;
    private String fullName;
    private OffsetDateTime purchaseAt;

    // EVENT
    private String landingSlug;
    private String planKey;
    private String planTitle;
    private Integer quantity;
    private String couponCode;

    // Totales
    private String currency;
    private BigDecimal unitPriceShown;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal commission;
    private BigDecimal total;

    // Info
    private boolean emailed;  // true si se envió email (primera vez)
}