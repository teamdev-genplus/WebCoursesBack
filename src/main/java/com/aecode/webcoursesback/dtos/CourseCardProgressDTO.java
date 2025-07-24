package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCardProgressDTO {
    private Long courseId;
    private String principalImage;
    private String title;
    private Integer orderNumber;
    private String urlnamecourse;
    private boolean completed;
}
