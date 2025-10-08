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
    private String beforeEventText;   // texto largo
    private String duringEventText;   // texto largo

    // Datos de importe:
    private String currency;

    // Precios “de catálogo”
    private Double priceAmount;           // precio regular
    private Double promptPaymentPrice;    // precio pronto pago (si aplica)
    private Boolean promptPaymentEnabled; // flag de pronto pago

    // Precio que realmente se usó para cálculos (según flag)
    private Double effectiveUnitPrice;

    // Totales calculados:
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;

    // Parámetros de cálculo:
    private Double taxRate;           // ej. 0.18
    private Boolean priceIncludesTax; // true si el precio ya incluye IGV
}
