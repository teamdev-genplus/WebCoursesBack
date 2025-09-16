package com.aecode.webcoursesback.dtos;
import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighlightedCourseDTO {

    private Long courseId;
    private String title;
    private String description;
    private String highlightImage;
    private LocalDate startDate;
}
