package com.aecode.webcoursesback.dtos.Izipay;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormTokenCreateRequest {
    private String orderId;        // requerido
    private Integer amountCents;   // requerido (en centavos)
    private String currency;       // "PEN" o "USD" (requerido)

    // Usuario
    private String clerkId;        // recomendado
    private String email;          // si no llega, lo buscamos por clerkId

    // Datos opcionales (si quieres enviarlos a Izipay en customer/billingDetails)
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String identityType;   // "DNI" etc (si aplica)
    private String identityCode;   // número doc (si aplica)
    private String address;
    private String country;        // "PE"
    private String city;           // "Lima"
    private String state;          // "Lima"
    private String zipCode;        // "15000"

    // NUEVO: lo que el usuario compra (módulos)
    private List<Long> moduleIds;

    // ===== NUEVO: dominio de orden =====
    // "MODULES" (por defecto si no envían) o "EVENT"
    private String domain;
    // ===== NUEVO: para EVENT =====
    private String landingSlug;    // ej. "ai-construction-summit-2025"
    private String landingPlanKey; // ej. "regular" | "comunidad" | "corporativo"
    private Integer landingQuantity;   // opcional (usa default por plan si null)
    private String  landingCouponCode; // opcional
    private String landingModality;
    private String groupId;
}
