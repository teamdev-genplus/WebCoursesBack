package com.aecode.webcoursesback.dtos.Paid.Voucher;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManualPaymentVoucherDTO {
    private Long id;
    private String clerkId;
    private String voucherUrl;
    private List<Long> moduleIds;     // módulos tal cual vinieron en el registro
    private String paymentMethod;
    private String status;            // "PAID" | "PENDING"
    private OffsetDateTime paidAt;
    private boolean validated;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // ===== NUEVO: reporte por validación parcial (solo se usa en respuestas de setValidated) =====
    private List<Long> acceptedModuleIds;     // los que se exportaron a la tabla unificada
    private List<Long> skippedAlreadyOwned;   // ya tenía acceso
    private List<Long> skippedNotFound;       // módulo inexistente
    private String infoMessage;               // mensaje resumido para el admin
}