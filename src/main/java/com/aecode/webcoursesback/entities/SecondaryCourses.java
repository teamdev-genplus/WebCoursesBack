package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;
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
@Entity
@Table(name = "secondary_courses")
@SequenceGenerator(name = "secondary_courses_seq", sequenceName = "secondary_courses_sequence", allocationSize = 1)
public class SecondaryCourses {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "secondary_courses_seq")
    private int seccourseId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String programTitle;

    @Column(length = 50)
    private String module;

    @Column(length = 255)
    private String brochureUrl;

    @Column(length = 255)
    private String startDate;

    @Column(length = 255)
    private String certificateHours;

    @Column(length = 255)
    private Double priceRegular;

    @Column()
    private int discountPercentage;

    @Column(length = 255)
    private Double promptPaymentPrice;

    @Column()
    private Boolean isOnSale;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Mode mode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String achievement;

    @Column(length = 255)
    private String videoUrl;

    @Column(length = 255)
    private String principalimage;

    @Column()
    private int totalHours;

    @Column()
    private int numberOfSessions;

    @Column()
    private int numberOfUnits;

    @Column()
    private String[] schedules;

    @Column()
    private String[] requirements;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "secondcourse_benefits", joinColumns = @JoinColumn(name = "seccourse_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "secondcourse_tools", joinColumns = @JoinColumn(name = "seccourse_id"), inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private List<Tool> tools;

    @Builder.Default
    @OneToMany(mappedBy = "secondary_course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyPlan> studyplans = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "secondary_course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "secondcourse_freqquests", joinColumns = @JoinColumn(name = "seccourse_id"), inverseJoinColumns = @JoinColumn(name = "freqquest_id"))
    private List<FreqQuest> freqquests;

    public enum Mode {
        SINCRONO("Síncrono"),
        ASINCRONO("Asíncrono"),
        EN_VIVO("En Vivo");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}