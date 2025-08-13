package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Coupon.*;
import com.aecode.webcoursesback.services.ICouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final ICouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<CouponValidateResponseDTO> validateCoupon(@RequestBody CouponValidateRequestDTO request) {
        return ResponseEntity.ok(couponService.validateCoupon(request));
    }

    @PostMapping("/apply")
    public ResponseEntity<CouponApplyResponseDTO> applyCoupon(@Valid @RequestBody CouponApplyRequestDTO request) {
        CouponApplyResponseDTO response = couponService.applyCoupon(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping
    public ResponseEntity<CouponResponseDTO> createCoupon(@RequestBody CouponCreateRequestDTO request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }
}
