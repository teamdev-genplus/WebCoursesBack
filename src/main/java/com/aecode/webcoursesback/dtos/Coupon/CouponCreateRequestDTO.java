package com.aecode.webcoursesback.dtos.Coupon;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponCreateRequestDTO {
    private String code;
    private String description;
    private Double discountPercentage;
    private Double discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;          // null = ilimitado
    private Boolean singleUsePerUser;    // null = false
    private Boolean active;              // null = true

    // ===== Cursos (legacy) =====
    private Boolean courseSpecific;            // null/false = general
    private List<Long> applicableCourseIds;    // requerido si courseSpecific = true

    // ===== NUEVO: Landing =====
    private Boolean landingSpecific;     // true => aplica sólo a una landing
    private String landingSlug;          // requerido si landingSpecific = true
}
