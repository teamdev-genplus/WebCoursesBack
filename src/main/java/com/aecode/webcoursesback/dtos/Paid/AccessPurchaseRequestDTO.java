package com.aecode.webcoursesback.dtos.Paid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessPurchaseRequestDTO {
    /** Compatibilidad: puedes aceptar clerkId por query; ideal: tomarlo del JWT */
    @NotBlank
    private String clerkId;

    @NotBlank
    private String purchaseNumber;           // único (idempotente)

    private OffsetDateTime purchaseAt;       // opcional; si null => server now()
    private String purchaseDateLabel;        // opcional; ej. "22 de Agosto del 2025"

    @NotEmpty
    private List<Long> moduleIds;            // módulos a conceder

    @NotNull @Digits(integer = 12, fraction = 2)
    private BigDecimal total;                // total pagado final

    @Digits(integer = 12, fraction = 2)
    private BigDecimal discount;             // opcional, 0.00 si no aplica

    @Digits(integer = 12, fraction = 2)
    private BigDecimal commission;           // opcional, 0.00 si no aplica

    @Digits(integer = 12, fraction = 2)
    private BigDecimal subtotal;             // opcional (para mostrar misma fila)

    @NotNull
    private PaymentMethod method;            // PAYPAL, YAPE, PLIN

    @NotNull
    private CurrencyCode currency;           // PEN, USD

    public enum PaymentMethod { PAYPAL, YAPE, PLIN }
    public enum CurrencyCode { PEN, USD }
}
