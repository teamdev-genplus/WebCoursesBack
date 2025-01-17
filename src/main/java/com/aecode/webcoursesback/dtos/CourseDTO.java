package com.aecode.webcoursesback.dtos;

import java.util.List;

public class CourseDTO {
    private int courseId;
    private String title;
    private String coverimage;
    private String gift;
    private String moduleimage;
    private String urlkit;
    private String videoUrl;
    private Integer percentage;
    private Double price;
    private Integer hours;
    private List<ModuleDTO> modules;
    private List<Integer> toolIds;
    private List<ToolDTO> tools;

    private String subtitle;

    public String getUrlkit() {
        return urlkit;
    }

    public void setUrlkit(String urlkit) {
        this.urlkit = urlkit;
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
