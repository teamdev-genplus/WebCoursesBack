package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Coupon.CouponApplyRequestDTO;
import com.aecode.webcoursesback.dtos.Coupon.CouponApplyResponseDTO;
import com.aecode.webcoursesback.services.ICouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final ICouponService couponService;

    @PostMapping("/apply")
    public ResponseEntity<CouponApplyResponseDTO> applyCoupon(@Valid @RequestBody CouponApplyRequestDTO request) {
        CouponApplyResponseDTO response = couponService.applyCoupon(request);
        return ResponseEntity.ok(response);
    }
}
