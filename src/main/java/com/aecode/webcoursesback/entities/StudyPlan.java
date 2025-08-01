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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(length = 255)
    private String unit;

    @Column(length = 255)
    private int hours;

    @Column(nullable = true, length = 255)
    private int orderNumber;
    //NUEVCOS ATRIBUTOS PARA EL ACCESO A CURSO DEL MODULO COMPRADO
    @Column(length = 255)
    private String urlrecording;
    @Column(length = 255)
    private String downloadmaterial;
    @Column(length = 255)
    private String viewpresentation;

    @ElementCollection
    @CollectionTable(name = "studyplan_session", joinColumns = @JoinColumn(name = "studyplan_id"))
    @Column(name = "session",columnDefinition = "TEXT")
    private List<String> sessions = new ArrayList<>();


}
