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
    private Long seccourseId;
    private int moduleId;
    private String unit;
    private int hours;
    private int orderNumber;
    private List<String> sessions;
    private String urlrecording;
    private String dmaterial;
    private String viewpresentation;

}
