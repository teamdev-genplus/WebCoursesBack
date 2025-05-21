package com.aecode.webcoursesback.dtos;

import com.aecode.webcoursesback.entities.Module;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ModuleDetailDTO {

    private int moduleId;
    private String courseTitle;  // Desde Course
    private String title;
    private String programTitle;
    private String description;
    private String brochureUrl;
    private String whatsappGroupLink;
    private LocalDate startDate;
    private String certificateHours;
    private Double priceRegular;
    private Double discountPercentage;
    private Double promptPaymentPrice;
    private Boolean isOnSale;
    private String achievement;
    private String principalImage;
    private Integer orderNumber;
    private Module.Mode mode;
    private String urlName;
    private String type;

    private List<moduleBenefitsDTO> benefits;
    private List<ToolDTO> tools;
    private List<StudyPlanDTO> studyPlans;
    private List<CouponDTO> coupons;
    private List<FreqQuestDTO> freqquests;
    private List<CourseTagDTO> tags;
    private List<CertificateDTO> certificates;

    private String urlMaterialAccess;
    private String urlJoinClass;
}