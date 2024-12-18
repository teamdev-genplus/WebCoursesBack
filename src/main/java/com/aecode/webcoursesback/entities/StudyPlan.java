package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "studyplans")
@SequenceGenerator(name = "studyplan_seq", sequenceName = "studyplan_sequence", allocationSize = 1)
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studyplan_seq")
    private int studyplanId;

    @Column(length = 255)
    private String unit;

    @ManyToOne
    @JoinColumn(name = "seccourse_id", nullable = false)
    private SecondaryCourses secondary_course;

    @Column(length = 255)
    private int hours;

    @ElementCollection
    @CollectionTable(name = "studyplan_session", joinColumns = @JoinColumn(name = "studyplan_id"))
    @Column(name = "session")
    private List<String> sessions = new ArrayList<>();


    public StudyPlan() {
    }

    public StudyPlan(int studyplanId, String unit, SecondaryCourses secondary_course, int hours, List<String> sessions) {
        this.studyplanId = studyplanId;
        this.unit = unit;
        this.secondary_course = secondary_course;
        this.hours = hours;
        this.sessions = sessions;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public SecondaryCourses getSecondary_course() {
        return secondary_course;
    }

    public void setSecondary_course(SecondaryCourses secondary_course) {
        this.secondary_course = secondary_course;
    }

    public int getStudyplanId() {
        return studyplanId;
    }

    public void setStudyplanId(int studyplanId) {
        this.studyplanId = studyplanId;
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
