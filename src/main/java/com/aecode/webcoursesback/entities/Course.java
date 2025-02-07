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

    @Column(nullable = true, length = 255)
    private String description;

    @Column(length = 255)
    private String coverimage;

    @Column(length = 255)
    private String gift;

    @Column(length = 255)
    private String moduleimage;

    @Column(length = 255)
    private String urlkit;

    @Column(length = 255)
    private String videoUrl;
    @Column
    private Integer percentage;
    @Column
    private Double price;
    @Column
    private Integer hours;

    @Column
    private Integer courseOrder;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_tools", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private List<Tool> tools;

    @Column(length = 255)
    private String subtitle;

    public Course() {
    }

    public Course(int courseId, String title, String description, String coverimage, String gift, String moduleimage,
            String urlkit, String videoUrl, Integer percentage, Double price, List<Module> modules, Integer hours,
            List<Tool> tools, String subtitle, Integer courseOrder) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.coverimage = coverimage;
        this.gift = gift;
        this.moduleimage = moduleimage;
        this.urlkit = urlkit;
        this.videoUrl = videoUrl;
        this.percentage = percentage;
        this.price = price;
        this.modules = modules;
        this.hours = hours;
        this.tools = tools;
        this.subtitle = subtitle;
        this.courseOrder = courseOrder;
    }

    public Integer getCourseOrder() {
        return courseOrder;
    }

    public void setCourseOrder(Integer courseOrder) {
        this.courseOrder = courseOrder;
    }

    public String getUrlkit() {
        return urlkit;
    }

    public void setUrlkit(String urlkit) {
        this.urlkit = urlkit;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getModuleimage() {
        return moduleimage;
    }

    public void setModuleimage(String moduleimage) {
        this.moduleimage = moduleimage;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
