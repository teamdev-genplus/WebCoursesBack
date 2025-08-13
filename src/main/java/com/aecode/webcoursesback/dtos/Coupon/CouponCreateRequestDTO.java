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
    private Integer usageLimit;
    private Boolean singleUsePerUser;
    private Boolean courseSpecific;
    private Boolean active;
    private List<Long> applicableCourseIds;
}
