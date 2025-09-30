package com.aecode.webcoursesback.dtos.Izipay;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidatePaymentResponse {
    private boolean valid;        // true si firma correcta
    private String orderId;       // extraído de kr-answer
    private String orderStatus;   // PAID / UNPAID

    // NUEVO: que se concedió y que se omitió por duplicado
    private List<Long> grantedModuleIds;
    private List<Long> skippedModuleIds;
}