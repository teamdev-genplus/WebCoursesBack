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

public class MyModuleDTO {

    private int moduleId;
    private String title;
    private String programTitle;
    private String description;
    private String module;
    private String whatsappGroupLink;
    private List<StudyPlanDTO> studyPlans;
    private String urlMaterialAccess;
    private String urlJoinClass;
    private List<CertificateDTO> certificates;
    private String courseTitle;  // Desde Course
    private String type;
}