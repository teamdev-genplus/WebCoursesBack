package com.aecode.webcoursesback.dtos;

import com.aecode.webcoursesback.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondCourseDTO {
    private Long seccourseId;
    private String title;
    private String programTitle;
    private String description;
    private String module;
    private String brochureUrl;
    private String whatsappGroupLink;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private String certificateHours;
    private Double priceRegular;
    private Double discountPercentage;
    private Double promptPaymentPrice;
    private Boolean isOnSale;
    private String achievement;
    private String videoUrl;
    private String principalimage;
    private int totalHours;
    private String type;
    private int numberOfSessions;
    private int numberOfUnits;
    private String urlname;
    private Integer orderNumber;
    private String[] schedules;
    private String[] requirements;
    @Builder.Default
    private List<String> benefits = new ArrayList<>();
    @Builder.Default
    private List<ToolDTO> tools = new ArrayList<>();
    @Builder.Default
    private List<StudyPlanDTO> studyplans = new ArrayList<>();
    @Builder.Default
    private List<FreqQuestDTO> freqquests = new ArrayList<>();
    private SecondaryCourses.Mode mode;
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    //
    private String urlmaterialaccess;
    private String urljoinclass;
    @Builder.Default
    private List<String> certificateUrls = new ArrayList<>();
}
