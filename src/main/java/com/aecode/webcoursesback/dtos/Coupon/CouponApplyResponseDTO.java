package com.aecode.webcoursesback.dtos.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponApplyResponseDTO {
    private Double originalPrice;
    private Double discount;
    private Double finalPrice;
    private Boolean couponValid;
    private String message;
}
