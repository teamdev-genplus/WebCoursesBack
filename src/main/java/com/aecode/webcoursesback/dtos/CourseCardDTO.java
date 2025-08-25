package com.aecode.webcoursesback.dtos;
import com.aecode.webcoursesback.entities.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCardDTO {
    private Long courseId;
    private String principalImage;
    private String title;
    private Integer orderNumber;
    private String type;
    private Integer cantModOrHours;
    private Course.Mode mode;
    private String urlnamecourse;

    //NUEVO
    private boolean favorite;
}
