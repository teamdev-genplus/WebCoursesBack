package com.aecode.webcoursesback.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanDTO {

    private int studyplanId;
    private Long moduleId;
    private String unit;
    private int hours;
    private int orderNumber;
    private String urlrecording;
    private String downloadmaterial;
    private String viewpresentation;
    private List<String> sessions;
}
