package com.aecode.webcoursesback.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "courses")
@SequenceGenerator(name = "course_seq", sequenceName = "course_sequence", allocationSize = 1)
public class Course{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_seq")
    private Long courseId;

    @Column( length = 255)
    private String title;

    @Column( length = 255)
    private String tagcourse;//detail

    @Column( length = 255)
    private String titledescription;//detail

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column( length = 255)
    private String namebuttoncommunity;//detail

    @Column( length = 255)
    private String urlbuttoncommunity;//detail

    @Column
    private String availableorlaunching; //detail

    @Column( length = 255)
    private String urlbrochure; //detail

    @Column(length = 255)
    private String principalImage;

    @Column
    private String type;

    @Column
    private Integer orderNumber;

    @Column
    private Integer cantModOrHours; //para card

    @Column
    private Integer cantTotalHours; //para filtro

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Mode mode;

    //NUEVOS ATRIBUTOS PARA DESTACADO
    @Column(length = 255)
    private String highlightImage;  // URL o path de la imagen para destacado

    @Column
    private boolean highlighted = false; // si el curso está destacado


    public enum Mode {
        ASINCRONO,
        ENVIVO,
        PROXIMO,
        GRATUITO,
        MIXTO
    }

    // Relaciones
    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags= new ArrayList<>();


}
