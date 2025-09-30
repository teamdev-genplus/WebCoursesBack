package com.aecode.webcoursesback.entities;
import com.aecode.webcoursesback.entities.Coupon.Coupon;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "modules")
@SequenceGenerator(name = "module_seq", sequenceName = "module_sequence", allocationSize = 1)
public class Module extends BaseProduct{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "module_seq")
    private Long moduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = true)
    @JsonBackReference
    private Course course;

    @Column(length = 255)
    private String programTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Integer orderNumber;

    @Column
    private String textmode;

    //nuevos atributos de plan de estudio cuando es DIPLOMADO.
    @Column
    private Integer cantMods;

    @Column
    private Integer cantHours_live;

    @Column
    private Integer cantHours_asinc;

    @Column
    private Integer totalHours;

    //nuevos atributos de plan de estudio cuando es MODULAR.

    @Column
    private String titleStudyplan;

    @Column
    private String descriptionStudyplan;

    //newbottons que veran los usuarios en mis cursos CUANDO HAYAN COMPRADO EL MODULO
    @Column(length = 255)
    private String dmaterial;

    @Column(length = 255)
    private String urlJoinClass;

    //Newbotton para instructores
    @Column
    private String urlInstructors;

    //MODE
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Mode mode;

    @Column(columnDefinition = "TEXT")
    private String descriptionbyCertificate;

    public enum Mode {
        ASINCRONO,
        ENVIVO,
        PROXIMO,
        GRATUITO,
        MIXTO,
        EXCLUSIVO
    }

    //NUEVOS ATRIBUTOS PARA EL MODO EXCLUSIVO
    @Column(length = 255)
    private String urlimagelogo1;
    @Column(length = 255)
    private String urlimagelogo2;

    //NUEVOS ATRIBUTOS PARA ACCESO AL CUROS
    @Column(length = 255)
    private String urlviewsyllabus;
    @Column(length = 255)
    private String urlviewcontent;
    @Column(length = 255)
    private String urlrecording;
    @Column(length = 255)
    private String viewpresentation;

    // === APIKEY DE ASISTENTES DE BOT (ENCRIPTADA) ===
    @Column(name = "assistant_api_key_enc", columnDefinition = "TEXT")
    private String assistantApiKeyEnc;


    //Relaciones
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "module_tools",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "tool_id"))
    private List<Tool> tools= new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    private List<StudyPlan> studyPlans= new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "module_freqquests",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "freqquest_id"))
    private List<FreqQuest> freqquests= new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "module_tags",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags= new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "module_instructors",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id"))
    private List<Instructor> instructors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates= new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Requirement> requirements = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Benefits> benefits = new ArrayList<>();

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    @Builder.Default
    private List<ModuleResourceLink> resourceLinks = new ArrayList<>();

}
