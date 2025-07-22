package com.aecode.webcoursesback.dtos;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import java.time.LocalDate;

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

    //Nuevo Atributo 22/07/25
    private Integer textmode;
    //nuevos atributos de plan de estudio cuando es DIPLOMADO.
    private Integer cantMods;
    private Integer cantHours1;
    private Integer cantHours2;
    //nuevos atributos de plan de estudio cuando es MODULAR.
    private String titleStudyplan;
    private String descriptionStudyplan;
    //newbottons que veran los usuarios en mis cursos CUANDO HAYAN COMPRADO EL MODULO
    private String urlrecording;
    private String dmaterial;
    private String viewpresentation;
    private String urlJoinClass;

    //Newbotton para instructores
    private String urlInstructors;


    // Campos heredados de BaseProduct
    private String brochureUrl;
    private String whatsappGroupLink;
    private LocalDate startDate;
    private String urlName;
    private Double priceRegular;
    private Double discountPercentage;
    private Double promptPaymentPrice;
    private Boolean isOnSale;
    private String achievement;

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
    @Builder.Default
    private List<InstructorDTO> instructors= new ArrayList<>();

    // Lista para navegación (módulos hermanos)
    @Builder.Default
    private List<ModuleListDTO> courseModules= new ArrayList<>();
}

