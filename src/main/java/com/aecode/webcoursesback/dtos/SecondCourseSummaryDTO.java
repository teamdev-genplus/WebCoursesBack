package com.aecode.webcoursesback.dtos;

import java.time.LocalDate;

import com.aecode.webcoursesback.entities.SecondaryCourses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecondCourseSummaryDTO {
    private Long seccourseId;
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
    private String principalimage;
    private Integer orderNumber;
    private SecondaryCourses.Mode mode;

}
