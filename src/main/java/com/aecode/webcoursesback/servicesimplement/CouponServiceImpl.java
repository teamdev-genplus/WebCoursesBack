package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.Coupon.*;
import com.aecode.webcoursesback.entities.Coupon.Coupon;
import com.aecode.webcoursesback.entities.Coupon.CouponRedemption;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.repositories.Coupon.CouponRedemptionRepository;
import com.aecode.webcoursesback.repositories.Coupon.CouponRepository;
import com.aecode.webcoursesback.repositories.ICourseRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.ICouponService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final ICourseRepo courseRepository;
    private final IUserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public CouponApplyResponseDTO applyCoupon(CouponApplyRequestDTO request) {
        if (!userProfileRepository.existsByClerkId(request.getClerkId())) {
            throw new EntityNotFoundException("Usuario con clerkId '" + request.getClerkId() + "' no encontrado");
        }


        //Si ingresan un cupón que no existe, lanza una excepcion
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new EntityNotFoundException("El cupón ingresado no es válido"));

        LocalDate today = LocalDate.now();


        // Suponiendo que usageCount y usageLimit son no nulos (Integer), revisamos límites:
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("El cupón ha alcanzado su límite de uso");
        }

        // Si no tienes campo active en Coupon, elimina esta validación o agrégalo en la entidad
        if (coupon.getActive() != null && !coupon.getActive()) {
            throw new IllegalArgumentException("El cupón no está activo");
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

        if (today.isBefore(coupon.getStartDate())) {
            throw new IllegalArgumentException(
                    "Este cupón no es válido hasta el " + coupon.getStartDate().format(formatter)
            );
        }

        if (today.isAfter(coupon.getEndDate())) {
            throw new IllegalArgumentException(
                    "Este cupón expiró el " + coupon.getEndDate().format(formatter)
            );
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

        return new CouponApplyResponseDTO(originalPrice, discount, finalPrice, true, "¡Cupón aplicado exitosaente!");
    }

    @Override
    @Transactional
    public CouponValidateResponseDTO validateCoupon(CouponValidateRequestDTO request) {
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new EntityNotFoundException("El cupón ingresado no es válido"));

        LocalDate today = LocalDate.now();

        // Formato de fecha en español
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

        if (coupon.getActive() != null && !coupon.getActive()) {
            return CouponValidateResponseDTO.builder()
                    .couponCode(coupon.getCode())
                    .couponValid(false)
                    .message("El cupón no está activo")
                    .build();
        }

        if (today.isBefore(coupon.getStartDate())) {
            return CouponValidateResponseDTO.builder()
                    .couponCode(coupon.getCode())
                    .couponValid(false)
                    .message("Este cupón no es válido hasta el " + coupon.getStartDate().format(formatter))
                    .build();
        }

        if (today.isAfter(coupon.getEndDate())) {
            return CouponValidateResponseDTO.builder()
                    .couponCode(coupon.getCode())
                    .couponValid(false)
                    .message("Este cupón expiró el " + coupon.getEndDate().format(formatter))
                    .build();
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() != null &&
                coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return CouponValidateResponseDTO.builder()
                    .couponCode(coupon.getCode())
                    .couponValid(false)
                    .message("El cupón ha alcanzado su límite de uso")
                    .build();
        }

        // Si es específico de curso, convertir las entidades a IDs
        List<Long> courseIds = null;
        if (Boolean.TRUE.equals(coupon.getCourseSpecific()) && coupon.getApplicableCourses() != null) {
            courseIds = coupon.getApplicableCourses().stream()
                    .map(Course::getCourseId) // O el nombre del método de ID en tu entidad Course
                    .toList();
        }

        return CouponValidateResponseDTO.builder()
                .couponCode(coupon.getCode())
                .discountPercentage(coupon.getDiscountPercentage())
                .discountAmount(coupon.getDiscountAmount())
                .couponValid(true)
                .message("¡Cupón aplicado exitosamente!")
                .applicableCourseIds(courseIds)
                .build();
    }
    @Override
    public List<CouponResponseDTO> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CouponResponseDTO createCoupon(CouponCreateRequestDTO request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountPercentage(request.getDiscountPercentage())
                .discountAmount(request.getDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .usageCount(0)
                .singleUsePerUser(request.getSingleUsePerUser())
                .courseSpecific(request.getCourseSpecific())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        if (request.getCourseSpecific() && request.getApplicableCourseIds() != null) {
            List<Course> courses = courseRepository.findAllById(request.getApplicableCourseIds());
            coupon.setApplicableCourses(courses);
        }

        Coupon saved = couponRepository.save(coupon);
        return mapToResponseDTO(saved);
    }

    private CouponResponseDTO mapToResponseDTO(Coupon coupon) {
        return CouponResponseDTO.builder()
                .couponId(coupon.getCouponId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountPercentage(coupon.getDiscountPercentage())
                .discountAmount(coupon.getDiscountAmount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .singleUsePerUser(coupon.getSingleUsePerUser())
                .courseSpecific(coupon.getCourseSpecific())
                .active(coupon.getActive())
                .applicableCourseNames(
                        coupon.getApplicableCourses() != null
                                ? coupon.getApplicableCourses().stream()
                                .map(Course::getTitle)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }


}
