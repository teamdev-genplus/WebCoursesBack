package com.aecode.webcoursesback.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SCMyCourseDTO {
    private Long seccourseId;
    private String title;
    private String programTitle;
    private String description;
    private String module;
    private String whatsappGroupLink;
    private String[] schedules;
    @Builder.Default
    private List<StudyPlanDTO> studyplans = new ArrayList<>();
    private String urlmaterialaccess;
    private String urljoinclass;
    @Builder.Default
    private List<String> certificateUrls = new ArrayList<>();

}
