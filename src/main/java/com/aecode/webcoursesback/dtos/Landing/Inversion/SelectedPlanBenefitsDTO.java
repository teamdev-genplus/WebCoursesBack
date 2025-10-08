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
    private Double priceAmount;       // precio base (subtotal si no incluye IGV)

    // Totales calculados (si decides que el back los calcule):
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private Double taxRate;           // ej. 0.18
    private Boolean priceIncludesTax; // true si priceAmount ya incluye IGV
}
