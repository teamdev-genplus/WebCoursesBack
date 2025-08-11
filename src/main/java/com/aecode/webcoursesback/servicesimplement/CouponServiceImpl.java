package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.Coupon.CouponApplyRequestDTO;
import com.aecode.webcoursesback.dtos.Coupon.CouponApplyResponseDTO;
import com.aecode.webcoursesback.entities.Coupon.Coupon;
import com.aecode.webcoursesback.entities.Coupon.CouponRedemption;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.repositories.Coupon.CouponRedemptionRepository;
import com.aecode.webcoursesback.repositories.Coupon.CouponRepository;
import com.aecode.webcoursesback.repositories.ICourseRepo;
import com.aecode.webcoursesback.services.ICouponService;
import com.google.api.gax.rpc.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final ICourseRepo courseRepository;

    @Override
    @Transactional
    public CouponApplyResponseDTO applyCoupon(CouponApplyRequestDTO request) {
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new EntityNotFoundException("Cupón no encontrado"));

        LocalDate today = LocalDate.now();

        // Suponiendo que usageCount y usageLimit son no nulos (Integer), revisamos límites:
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("El cupón ha alcanzado su límite de uso");
        }

        // Si no tienes campo active en Coupon, elimina esta validación o agrégalo en la entidad
        if (coupon.getActive() != null && !coupon.getActive()) {
            throw new IllegalArgumentException("El cupón no está activo");
        }

        if (today.isBefore(coupon.getStartDate()) || today.isAfter(coupon.getEndDate())) {
            throw new IllegalArgumentException("El cupón no está dentro de su rango de validez");
        }

        if (Boolean.TRUE.equals(coupon.getSingleUsePerUser())) {
            boolean usedByUser = redemptionRepository.existsByCouponAndClerkId(coupon, request.getClerkId());
            if (usedByUser) {
                throw new IllegalArgumentException("Este cupón ya fue usado por el usuario");
            }
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));

        if (Boolean.TRUE.equals(coupon.getCourseSpecific())) {
            if (coupon.getApplicableCourses() == null || !coupon.getApplicableCourses().contains(course)) {
                throw new IllegalArgumentException("Este cupón no es aplicable para este curso");
            }
        }

        Double originalPrice = course.getFullprice();
        Double discount = 0d;

        if (coupon.getDiscountPercentage() != null && coupon.getDiscountPercentage() > 0) {
            discount = originalPrice * (coupon.getDiscountPercentage() / 100);
        } else if (coupon.getDiscountAmount() != null && coupon.getDiscountAmount() > 0) {
            discount = coupon.getDiscountAmount();
        }

        Double finalPrice = originalPrice - discount;
        if (finalPrice < 0) {
            finalPrice = 0d;
        }

        // Incrementar contador de uso
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        // Registrar redención por usuario si aplica
        if (Boolean.TRUE.equals(coupon.getSingleUsePerUser())) {
            CouponRedemption redemption = CouponRedemption.builder()
                    .coupon(coupon)
                    .clerkId(request.getClerkId())
                    .redeemedAt(LocalDateTime.now())
                    .build();
            redemptionRepository.save(redemption);
        }

        return new CouponApplyResponseDTO(originalPrice, discount, finalPrice, true, "Cupón aplicado con éxito");
    }
}
