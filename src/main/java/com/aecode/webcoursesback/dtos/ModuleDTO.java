package com.aecode.webcoursesback.dtos;
import java.util.ArrayList;
import java.util.List;

import com.aecode.webcoursesback.entities.BaseProduct;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleDTO {
    private Long moduleId;
    private Long courseId;
    private String programTitle;
    private String description;
    private Integer orderNumber;


    // Campos heredados de BaseProduct
    private String brochureUrl;
    private String whatsappGroupLink;
    private LocalDate startDate;
    private String urlName;
    private String certificateHours;
    private Double priceRegular;
    private Double discountPercentage;
    private Double promptPaymentPrice;
    private Boolean isOnSale;
    private String achievement;
    private String urlMaterialAccess;
    private String urlJoinClass;

    // Relaciones específicas de Module
    @Builder.Default
    private List<ToolDTO> tools = new ArrayList<>();
    @Builder.Default
    private List<StudyPlanDTO> studyPlans = new ArrayList<>();
    @Builder.Default
    private List<CouponDTO> coupons = new ArrayList<>();
    @Builder.Default
    private List<FreqQuestDTO> freqquests = new ArrayList<>();
    @Builder.Default
    private List<TagDTO> tags = new ArrayList<>();
    @Builder.Default
    private List<CertificateDTO> certificates = new ArrayList<>();
    @Builder.Default
    private List<ScheduleDTO> schedules = new ArrayList<>();
    @Builder.Default
    private List<RequirementDTO> requirements = new ArrayList<>();

    // Lista para navegación (módulos hermanos)
    @Builder.Default
    private List<ModuleListDTO> courseModules= new ArrayList<>();
}

