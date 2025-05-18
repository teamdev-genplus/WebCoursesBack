package com.aecode.webcoursesback.dtos;
import com.aecode.webcoursesback.entities.Course.Mode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CourseSummaryDTO {
    private int courseId;
    private String title;
    private String programTitle;
    private String description;
    private String module;
    private LocalDate startDate;
    private String certificateHours;
    private Double priceRegular;
    private Double discountPercentage;
    private Double promptPaymentPrice;
    private Boolean isOnSale;
    private String principalImage;
    private Integer orderNumber;
    private Mode mode;
    private String urlName;
}
