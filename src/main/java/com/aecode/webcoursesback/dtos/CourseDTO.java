package com.aecode.webcoursesback.dtos;
import jakarta.persistence.Column;

import java.util.List;

public class CourseDTO {
    private int courseId;
    private String title;
    private String coverimage;
    private String videoUrl;
    private int percentage;
    private double price;
    private int hours;
    private List<ModuleDTO> modules;
    private List<Integer> toolIds;
    private List<ToolDTO> tools;

    private String subtitle;

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public List<Integer> getToolIds() {
        return toolIds;
    }

    public void setToolIds(List<Integer> toolIds) {
        this.toolIds = toolIds;
    }

    public List<ToolDTO> getTools() {
        return tools;
    }

    public void setTools(List<ToolDTO> tools) {
        this.tools = tools;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<ModuleDTO> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDTO> modules) {
        this.modules = modules;
    }
}
