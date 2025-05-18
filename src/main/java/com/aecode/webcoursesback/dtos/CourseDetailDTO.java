package com.aecode.webcoursesback.dtos;
import com.aecode.webcoursesback.entities.Course.Mode;
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
public class CourseDetailDTO {
    private int courseId;
    private String title;
    private String programTitle;
    private String description;
    private String module;
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
    private Mode mode;
    private String urlName;

    private List<String> benefits;
    private List<ToolDTO> tools;
    private List<StudyPlanDTO> studyPlans;
    private List<CouponDTO> coupons;
    private List<FreqQuestDTO> freqquests;
    private List<CourseTagDTO> tags;
    private List<CertificateDTO> certificates;

    private String urlMaterialAccess;
    private String urlJoinClass;
}
