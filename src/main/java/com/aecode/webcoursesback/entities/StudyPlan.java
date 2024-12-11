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

    @ManyToMany(mappedBy = "studyplans")
    private List<SecondaryCourses> secondary_courses;

    @ElementCollection
    @CollectionTable(name = "studyplan_session", joinColumns = @JoinColumn(name = "studyplan_id"))
    @Column(name = "session")
    private List<String> sessions = new ArrayList<>();


    public StudyPlan() {
    }

    public StudyPlan(int studyplanId, String unit, List<SecondaryCourses> secondary_courses, List<String> sessions) {
        this.studyplanId = studyplanId;
        this.unit = unit;
        this.secondary_courses = secondary_courses;
        this.sessions = sessions;
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

    public List<SecondaryCourses> getSecondary_courses() {
        return secondary_courses;
    }

    public void setSecondary_courses(List<SecondaryCourses> secondary_courses) {
        this.secondary_courses = secondary_courses;
    }

    public List<String> getSessions() {
        return sessions;
    }

    public void setSessions(List<String> sessions) {
        this.sessions = sessions;
    }
}
