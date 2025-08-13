package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Coupon.*;

import java.util.List;

public interface ICouponService {
    CouponApplyResponseDTO applyCoupon(CouponApplyRequestDTO request);
    CouponValidateResponseDTO validateCoupon(CouponValidateRequestDTO request);

    List<CouponResponseDTO> getAllCoupons();
    CouponResponseDTO createCoupon(CouponCreateRequestDTO request);
}
