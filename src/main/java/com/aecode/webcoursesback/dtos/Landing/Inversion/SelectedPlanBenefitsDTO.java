package com.aecode.webcoursesback.dtos.Landing.Inversion;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectedPlanBenefitsDTO {
    private String key;
    private String title;

    // Beneficios como párrafos:
    private String beforeEventText;
    private String duringEventText;

    // Datos de importe:
    private String currency;

    // Precios “de catálogo”
    private Double priceAmount;           // precio regular
    private Double promptPaymentPrice;    // precio pronto pago (si aplica)
    private Boolean promptPaymentEnabled; // flag de pronto pago


    // Entrada/estado:
    private Integer quantity;              // cantidad considerada en el cálculo
    private String couponCodeApplied;      // cupón válido aplicado (si hubo)

    // Desglose:
    private BigDecimal subtotal;               // priceAmount * qty
    private BigDecimal discountPromptPayment;  // (priceAmount - promptPaymentPrice) * qty si está activo
    private BigDecimal discountCoupon;         // descuento por cupón aplicado al subtotal
    private BigDecimal discountTotal;          // suma de todos los descuentos
    private BigDecimal total;                  // subtotal - discountTotal
}
