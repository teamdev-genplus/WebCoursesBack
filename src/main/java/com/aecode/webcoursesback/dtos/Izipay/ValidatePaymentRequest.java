package com.aecode.webcoursesback.dtos.Izipay;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidatePaymentRequest {
    private String krHash;    // "kr-hash"
    private String krAnswer;  // "kr-answer" (JSON string)
}
