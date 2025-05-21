package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "modules")
@SequenceGenerator(name = "module_seq", sequenceName = "module_sequence", allocationSize = 1)
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "module_seq")
    private int moduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(length = 255)
    private String programTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String brochureUrl;

    @Column(length = 255)
    private String whatsappGroupLink;

    @Column
    private LocalDate startDate;

    @Column(length = 100)
    private String urlName;

    @Column(length = 255)
    private String certificateHours;

    @Column
    private Double priceRegular;

    @Column
    private Double discountPercentage;

    @Column
    private Double promptPaymentPrice;

    @Column
    private Boolean isOnSale;

    @Column(columnDefinition = "TEXT")
    private String achievement;

    @Column(length = 255)
    private String principalImage;

    @Column
    private Integer orderNumber;

    @Column(length = 255)
    private String urlMaterialAccess;

    @Column(length = 255)
    private String urlJoinClass;

    @Column
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Mode mode;

    //Relaciones
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleBenefits> benefits = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "module_tools",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private List<Tool> tools;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    private List<StudyPlan> studyPlans;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons;

    @ManyToMany
    @JoinTable(name = "module_freqquests",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "freqquest_id"))
    private List<FreqQuest> freqquests;

    @ManyToMany
    @JoinTable(name = "module_tags",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<CourseTag> tags;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleRequirements> requirements = new ArrayList<>();

    public enum Mode {
        SINCRONO,
        ASINCRONO,
        EN_VIVO,
        HIBRIDO
    }
}
