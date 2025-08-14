package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCartDTO {
    private Long courseId;
    private String principalImage;
    private String title;
    private Integer cantTotalHours;
    private double discount;
    private double discountInPercentage;
    private double pricewithdiscount;
    private List<ModuleCartDTO> modules;
}
