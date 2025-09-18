package com.aecode.webcoursesback.entities;
import com.aecode.webcoursesback.entities.VClassroom.ModuleVideo;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "module_resource_links")
@SequenceGenerator(name = "mres_link_seq", sequenceName = "mres_link_sequence", allocationSize = 1)

public class ModuleResourceLink {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mres_link_seq")
    private Long id;

    /** Target a nivel módulo (dropdown general) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    /** Target a nivel video (materiales por video) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private ModuleVideo video;


    @Column(nullable = false, length = 255)
    private String name;

    /** Subtítulo (en tu diseño) */
    @Column(name = "subtitle", length = 255)
    private String subtitle;


    @Column(nullable = false, length = 1000)
    private String url;

    /** LINK = “Abrir”, DOWNLOAD = “Descargar” (front decide el texto) */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", length = 20, nullable = false)
    private ResourceType resourceType = ResourceType.LINK;


    @Column
    private Integer orderNumber; // para ordenar el dropdown

    @Column(nullable = false)
    private boolean active = true;

    public enum ResourceType { LINK, DOWNLOAD }

    /** Validación simple: al menos module o video deben estar presentes */
    @PrePersist @PreUpdate
    private void validateTarget() {
        if (module == null && video == null) {
            throw new IllegalStateException("ModuleResourceLink: debe tener module o video asignado.");
        }
    }
}
