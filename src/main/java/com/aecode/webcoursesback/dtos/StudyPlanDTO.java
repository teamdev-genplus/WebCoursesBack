package com.aecode.webcoursesback.dtos;

import java.util.List;

public class StudyPlanDTO {

    private int studyplanId;
    private int seccourseId;
    private String unit;
    private int hours;
    private List<String> sessions;

    public int getStudyplanId() {
        return studyplanId;
    }

    public void setStudyplanId(int studyplanId) {
        this.studyplanId = studyplanId;
    }

    public int getSeccourseId() {
        return seccourseId;
    }

    public void setSeccourseId(int seccourseId) {
        this.seccourseId = seccourseId;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getSessions() {
        return sessions;
    }

    public void setSessions(List<String> sessions) {
        this.sessions = sessions;
    }
}
