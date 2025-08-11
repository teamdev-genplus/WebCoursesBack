package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Coupon.CouponApplyRequestDTO;
import com.aecode.webcoursesback.dtos.Coupon.CouponApplyResponseDTO;

public interface ICouponService {
    CouponApplyResponseDTO applyCoupon(CouponApplyRequestDTO request);
}
