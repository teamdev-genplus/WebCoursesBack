package com.aecode.webcoursesback.dtos.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponValidateResponseDTO {
    private String couponCode;
    private Double discountPercentage;
    private Double discountAmount;
    private Boolean couponValid;
    private String message;
}
