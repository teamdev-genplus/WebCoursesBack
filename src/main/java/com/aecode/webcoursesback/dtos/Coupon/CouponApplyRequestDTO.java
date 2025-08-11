package com.aecode.webcoursesback.dtos.Coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponApplyRequestDTO {
    private String clerkId;
    private Long courseId;
    private String couponCode;
}
