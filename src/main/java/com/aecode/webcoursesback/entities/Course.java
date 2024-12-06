package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@SequenceGenerator(name = "course_seq", sequenceName = "course_sequence", allocationSize = 1)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_seq")
    private int courseId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column( length = 255)
    private String coverimage;

    @Column( length = 255)
    private String principalimage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column( length = 255)
    private String videoUrl;

    @Column(length = 255)
    private Double price;

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
    @CollectionTable(name = "course_benefits", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    @Column(length = 255)
    private String schedule;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "course_tools",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tool_id")
    )
    private List<Tool> tools;

    @ManyToMany
    @JoinTable(
            name = "course_freqquests",
            joinColumns = @JoinColumn(name = "course_id"),
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

    public Course() {
    }

    public Course(int courseId, String title, String coverimage, String principalimage, String description, String videoUrl, Double price, Level level, Mode mode, String achievement, String exterallink, List<String> benefits, String schedule, List<Module> modules, List<Tool> tools, List<FreqQuest> freqquests) {
        this.courseId = courseId;
        this.title = title;
        this.coverimage = coverimage;
        this.principalimage = principalimage;
        this.description = description;
        this.videoUrl = videoUrl;
        this.price = price;
        this.level = level;
        this.mode = mode;
        this.achievement = achievement;
        this.exterallink = exterallink;
        this.benefits = benefits;
        this.schedule = schedule;
        this.modules = modules;
        this.tools = tools;
        this.freqquests = freqquests;
    }

    public List<FreqQuest> getFreqquests() {
        return freqquests;
    }

    public void setFreqquests(List<FreqQuest> freqquests) {
        this.freqquests = freqquests;
    }

    public String getExterallink() {
        return exterallink;
    }

    public void setExterallink(String exterallink) {
        this.exterallink = exterallink;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
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

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
