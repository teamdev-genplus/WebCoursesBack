package com.aecode.webcoursesback.dtos;
import com.aecode.webcoursesback.dtos.Profile.CourseUnitDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCardProgressDTO {
    private Long courseId;
    private String urlnamecourse;
    private String principalImage;
    private String title;
    private Integer orderNumber;
    private String type;                // "modular" / "diplomado" (del Course)
    private List<CourseUnitDTO> units;
}
