package com.aecode.webcoursesback.dtos.Coupon;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponValidateRequestDTO {
    private String couponCode;
}
