package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "secondary_courses")
@SequenceGenerator(name = "secondary_courses_seq", sequenceName = "secondary_courses_sequence", allocationSize = 1)
public class SecondaryCourses {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "secondary_courses_seq")
        private int seccourseId;

        @Column(nullable = false, length = 255)
        private String title;

        @Column( length = 255)
        private String principalimage;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Column( length = 255)
        private String videoUrl;

        @Column(length = 255)
        private Double priceRegular;

        @Column(length = 255)
        private Double priceAcademy;

        @Column()
        private int percentage;

        @Enumerated(EnumType.STRING)
        @Column(length = 50)
        private Level level;

        @Enumerated(EnumType.STRING)
        @Column(length = 50)
        private Mode mode;

        @Column(columnDefinition = "TEXT")
        private String achievement;

        @Column( length = 255)
        private String exterallink;

        @ElementCollection
        @CollectionTable(name = "secondcourse_benefits", joinColumns = @JoinColumn(name = "seccourse_id"))
        @Column(name = "benefit")
        private List<String> benefits = new ArrayList<>();

        @Column(length = 255)
        private String schedule;

        @ManyToMany
        @JoinTable(
                name = "secondcourse_tools",
                joinColumns = @JoinColumn(name = "seccourse_id"),
                inverseJoinColumns = @JoinColumn(name = "tool_id")
        )
        private List<Tool> tools;

        @OneToMany(mappedBy = "secondary_course", cascade = CascadeType.ALL, orphanRemoval = true)
            private List<StudyPlan> studyplans = new ArrayList<>();

    @OneToMany(mappedBy = "secondary_course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons = new ArrayList<>();


        @ManyToMany
        @JoinTable(
                name = "secondcourse_freqquests",
                joinColumns = @JoinColumn(name = "seccourse_id"),
                inverseJoinColumns = @JoinColumn(name = "freqquest_id")
        )
        private List<FreqQuest> freqquests;

        public enum Level {
            BASICO("Básico"),
            BASICO_INTERMEDIO("Básico Intermedio"),
            INTERMEDIO("Intermedio"),
            INTERMEDIO_AVANZADO("Intermedio Avanzado"),
            AVANZADO("Avanzado");

            private final String displayName;

            Level(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }

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

    public SecondaryCourses() {
    }

    public SecondaryCourses(int seccourseId, String title, String principalimage, String description, String videoUrl, Double priceRegular, int percentage, Double priceAcademy, Level level, Mode mode, String achievement, String exterallink, List<String> benefits, String schedule, List<Tool> tools, List<Coupon> coupons, List<StudyPlan> studyplans, List<FreqQuest> freqquests) {
        this.seccourseId = seccourseId;
        this.title = title;
        this.principalimage = principalimage;
        this.description = description;
        this.videoUrl = videoUrl;
        this.priceRegular = priceRegular;
        this.percentage = percentage;
        this.priceAcademy = priceAcademy;
        this.level = level;
        this.mode = mode;
        this.achievement = achievement;
        this.exterallink = exterallink;
        this.benefits = benefits;
        this.schedule = schedule;
        this.tools = tools;
        this.coupons = coupons;
        this.studyplans = studyplans;
        this.freqquests = freqquests;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Double getPriceRegular() {
        return priceRegular;
    }

    public void setPriceRegular(Double priceRegular) {
        this.priceRegular = priceRegular;
    }

    public Double getPriceAcademy() {
        return priceAcademy;
    }

    public void setPriceAcademy(Double priceAcademy) {
        this.priceAcademy = priceAcademy;
    }

    public List<StudyPlan> getStudyplans() {
        return studyplans;
    }

    public void setStudyplans(List<StudyPlan> studyplans) {
        this.studyplans = studyplans;
    }

    public int getSeccourseId() {
        return seccourseId;
    }

    public void setSeccourseId(int seccourseId) {
        this.seccourseId = seccourseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrincipalimage() {
        return principalimage;
    }

    public void setPrincipalimage(String principalimage) {
        this.principalimage = principalimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getExterallink() {
        return exterallink;
    }

    public void setExterallink(String exterallink) {
        this.exterallink = exterallink;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public List<FreqQuest> getFreqquests() {
        return freqquests;
    }

    public void setFreqquests(List<FreqQuest> freqquests) {
        this.freqquests = freqquests;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }
}
