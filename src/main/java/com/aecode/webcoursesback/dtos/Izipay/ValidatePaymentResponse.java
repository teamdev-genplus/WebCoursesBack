package com.aecode.webcoursesback.dtos.Izipay;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidatePaymentResponse {
    private boolean valid;        // true si firma correcta
    private String orderId;       // extraído de kr-answer
    private String orderStatus;   // PAID / UNPAID
}