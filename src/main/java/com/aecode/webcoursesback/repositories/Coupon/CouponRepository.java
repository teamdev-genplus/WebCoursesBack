package com.aecode.webcoursesback.repositories.Coupon;

import com.aecode.webcoursesback.entities.Coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
}
