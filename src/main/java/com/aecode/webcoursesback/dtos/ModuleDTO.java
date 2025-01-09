package com.aecode.webcoursesback.dtos;
import java.util.List;

public class ModuleDTO {
    private int moduleId;
    private int courseId;
    private String title;
    private String videoUrl;
    private int hours;
    private int percentage;
    private double price;
    private int orderNumber;
    private List<UnitDTO> units;
    private RelatedWorkDTO relatedworks;
    private String moduleimage;

    public String getModuleimage() {
        return moduleimage;
    }

    public void setModuleimage(String moduleimage) {
        this.moduleimage = moduleimage;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
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

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<UnitDTO> getUnits() {
        return units;
    }

    public void setUnits(List<UnitDTO> units) {
        this.units = units;
    }

    public RelatedWorkDTO getRelatedworks() {
        return relatedworks;
    }

    public void setRelatedworks(RelatedWorkDTO relatedworks) {
        this.relatedworks = relatedworks;
    }
}

