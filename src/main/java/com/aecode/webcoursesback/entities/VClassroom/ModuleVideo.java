package com.aecode.webcoursesback.entities.VClassroom;
import com.aecode.webcoursesback.entities.Module;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "module_videos",
        uniqueConstraints = @UniqueConstraint(name = "uq_module_video_order", columnNames = {"module_id","order_number"}))
@SequenceGenerator(name = "module_video_seq", sequenceName = "module_video_seq", allocationSize = 1)

public class ModuleVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "module_video_seq")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false, foreignKey = @ForeignKey(name = "fk_modulevideo_module"))
    private Module module;

    /** Título que verá el usuario en el card, ej: "Sesión 1" o "Clase 1" */
    @Column(name = "session_title", nullable = false, length = 120)
    private String sessionTitle;

    /** Etiqueta bajo el título en el card, ej: "Introducción a BIM" */
    @Column(name = "session_label", length = 255)
    private String sessionLabel;

    /** Orden en la lista (obligatorio) */
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    /** URL de reproducción (YouTube/Vimeo/MP4 firmado, etc.) */
    @Column(name = "video_url", nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    /** Miniatura del card (opcional) */
    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;

    /** Resumen/descripcion grande que va debajo del player (texto largo) */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Enlace único de “Materiales” para este video */
    @Column(name = "material_url", columnDefinition = "TEXT")
    private String materialUrl;
}
