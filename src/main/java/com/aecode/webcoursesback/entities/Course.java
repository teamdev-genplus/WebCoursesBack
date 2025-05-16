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
@Table(name = "courses")
@SequenceGenerator(name = "course_seq", sequenceName = "course_sequence", allocationSize = 1)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_seq")
    private int courseId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String programTitle;

    @Column(length = 50)
    private String module;

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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Mode mode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String achievement;

    @Column(length = 255)
    private String principalImage;

    @Column
    private Integer orderNumber;

    // URLs comunes para todos los usuarios
    @Column(length = 255)
    private String urlMaterialAccess;

    @Column(length = 255)
    private String urlJoinClass;


    // Relaciones

    @ElementCollection
    @CollectionTable(name = "benefits", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "benefit")
    private List<String> benefits;

    @ManyToMany
    @JoinTable(name = "course_tools",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private List<Tool> tools;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    private List<StudyPlan> studyPlans;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons;

    @ManyToMany
    @JoinTable(name = "course_freqquests",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "freqquest_id"))
    private List<FreqQuest> freqquests;

    @ManyToMany
    @JoinTable(name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<CourseTag> tags;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates;

    public enum Mode {
        SINCRONO,
        ASINCRONO,
        EN_VIVO,
        HIBRIDO
    }
}
