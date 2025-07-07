package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "instructors")
@SequenceGenerator(name = "instructor_seq", sequenceName = "instructor_sequence", allocationSize = 1)
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instructor_seq")
    private Long instructorId;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255)
    private String photoUrl;

    @ElementCollection
    @CollectionTable(name = "instructor_specialties", joinColumns = @JoinColumn(name = "instructor_id"))
    @Column(name = "specialty", length = 255)
    @Builder.Default
    private List<String> specialties = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "instructors")
    private List<Module> modules = new ArrayList<>();
}
