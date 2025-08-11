package com.aecode.webcoursesback.repositories.Coupon;
import com.aecode.webcoursesback.entities.Coupon.Coupon;
import com.aecode.webcoursesback.entities.Coupon.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long>{
    boolean existsByCouponAndClerkId(Coupon coupon, String clerkId);
}
