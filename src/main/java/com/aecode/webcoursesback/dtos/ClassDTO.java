package com.aecode.webcoursesback.dtos;
import java.util.Set;

public class ClassDTO {

    private int classId;
    private int moduleId;
    private String title;
    private String videoUrl;
    private String description;
    private String document;
    private int durationMinutes;
    private int orderNumber;
    private Set<ClassQuestionDTO> classquestions;


    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Set<ClassQuestionDTO> getClassquestions() {
        return classquestions;
    }

    public void setClassquestions(Set<ClassQuestionDTO> classquestions) {
        this.classquestions = classquestions;
    }
}
