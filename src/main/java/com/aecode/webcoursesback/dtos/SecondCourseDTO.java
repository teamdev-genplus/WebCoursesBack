package com.aecode.webcoursesback.dtos;

import com.aecode.webcoursesback.entities.SecondaryCourses;

import java.util.List;

public class SecondCourseDTO {

    private int seccourseId;
    private String title;
    private String principalimage;
    private String description;
    // Usado para POST/PATCH: IDs solamente
    private List<Integer> toolIds;
    private List<Integer> freqquestIds;

    private int percentage;

    private List<StudyPlanDTO> studyplans;
    private List<CouponDTO> coupons;

    // Usado para GET: objetos completos
    private List<ToolDTO> tools;
    private List<FreqQuestDTO> freqquests;

    private SecondaryCourses.Level level;

    private SecondaryCourses.Mode mode;

    private List<String> benefits;
    private String schedule;
    private String achievement;
    private String exterallink;
    private String videoUrl;
    private Double priceRegular;
    private Double priceAcademy;

    public List<CouponDTO> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponDTO> coupons) {
        this.coupons = coupons;
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

    public List<StudyPlanDTO> getStudyplans() {
        return studyplans;
    }

    public void setStudyplans(List<StudyPlanDTO> studyplans) {
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

    public List<Integer> getToolIds() {
        return toolIds;
    }

    public void setToolIds(List<Integer> toolIds) {
        this.toolIds = toolIds;
    }

    public List<Integer> getFreqquestIds() {
        return freqquestIds;
    }

    public void setFreqquestIds(List<Integer> freqquestIds) {
        this.freqquestIds = freqquestIds;
    }

    public List<ToolDTO> getTools() {
        return tools;
    }

    public void setTools(List<ToolDTO> tools) {
        this.tools = tools;
    }

    public List<FreqQuestDTO> getFreqquests() {
        return freqquests;
    }

    public void setFreqquests(List<FreqQuestDTO> freqquests) {
        this.freqquests = freqquests;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public SecondaryCourses.Level getLevel() {
        return level;
    }

    public void setLevel(SecondaryCourses.Level level) {
        this.level = level;
    }

    public SecondaryCourses.Mode getMode() {
        return mode;
    }

    public void setMode(SecondaryCourses.Mode mode) {
        this.mode = mode;
    }
}
