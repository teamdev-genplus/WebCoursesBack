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


        // ===== NUEVO: Política de participantes para el front =====
        /** "INCLUDE_BUYER" (general/aecoder) o "EXCLUDE_BUYER" (corporativo) */
        private String participantMode;

        /** Cantidad mínima permitida por plan (corporativo=2, otros=1) */
        private Integer minQuantity;

        /** Formularios que el front debe mostrar para esta cantidad */
        private Integer requiredParticipantForms;
    }
