package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(nullable = true, length = 255)
    private int orderNumber;

    @ElementCollection
    @CollectionTable(name = "studyplan_session", joinColumns = @JoinColumn(name = "studyplan_id"))
    @Column(name = "session")
    private List<String> sessions = new ArrayList<>();

}
