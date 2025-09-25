package com.aecode.webcoursesback.dtos.Paid.Voucher;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManualPaymentValidateRequest {
    private Boolean validated; // si true => dispara la inserción a UnifiedPaidOrder
}