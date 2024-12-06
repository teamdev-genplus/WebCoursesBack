package com.aecode.webcoursesback.dtos;
import com.aecode.webcoursesback.entities.Course;
import java.util.List;

public class CourseDTO {
    private int courseId;
    private String title;
    private String coverimage;
    private String principalimage;
    private String description;
    private Double price;
    private Course.Level level;
    private Course.Mode mode;
    // Usado para POST/PATCH: IDs solamente
    private List<Integer> toolIds;
    private List<Integer> freqquestIds;

    // Usado para GET: objetos completos
    private List<ToolDTO> tools;
    private List<FreqQuestDTO> freqquests;

    private List<String> benefits;
    private String schedule;
    private String achievement;
    private String exterallink;
    private String videoUrl;
    private List<ModuleDTO> modules;

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

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public String getExterallink() {
        return exterallink;
    }

    public void setExterallink(String exterallink) {
        this.exterallink = exterallink;
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

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
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

    public Course.Level getLevel() {
        return level;
    }

    public void setLevel(Course.Level level) {
        this.level = level;
    }

    public Course.Mode getMode() {
        return mode;
    }

    public void setMode(Course.Mode mode) {
        this.mode = mode;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ModuleDTO> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDTO> modules) {
        this.modules = modules;
    }
}
