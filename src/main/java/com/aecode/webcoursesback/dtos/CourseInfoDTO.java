package com.aecode.webcoursesback.dtos;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseInfoDTO {
    private Long courseId;
    private String title;
    private String tagcourse;
    private String titledescription;
    private String description;
    private String namebuttoncommunity;
    private String urlbuttoncommunity;
    private String availableorlaunching;
    private String urlbrochure;
    private String highlightImage;
}
