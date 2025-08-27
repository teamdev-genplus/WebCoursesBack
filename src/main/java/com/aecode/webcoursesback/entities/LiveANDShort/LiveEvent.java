package com.aecode.webcoursesback.entities.LiveANDShort;
import com.aecode.webcoursesback.entities.Instructor;
import com.aecode.webcoursesback.entities.Tag;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "live_events")
@SequenceGenerator(name = "live_event_seq", sequenceName = "live_event_sequence", allocationSize = 1)
public class LiveEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "live_event_seq")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    // Descripción corta exclusiva para el card destacado (hero)
    @Column(length = 500)
    private String featuredCardDescription;

    // Descripción larga para el detalle del live
    @Lob
    private String longDescription;

    @Column(nullable = false)
    private LocalDateTime startDateTime; // también es fecha límite de inscripción

    // Duración en minutos (visible cuando el live ya pasó)
    private Integer durationMinutes;

    // URL de imagen para destacado y para detalle (mismas)
    @Column(length = 1000)
    private String featuredImageUrl;

    // URL de imagen para cards generales (próximos / pasados)
    @Column(length = 1000)
    private String generalCardImageUrl;

    // Enlace de registro (cuando aún no inicia)
    @Column(length = 1000)
    private String registrationUrl;

    // Enlace del video (cuando ya pasó)
    @Column(length = 1000)
    private String playbackUrl;

    // Flags de control
    private boolean highlighted; // si aparece como destacado (hero)
    private boolean active = true; // soft-enable

    // Orden para carrusel de destacados
    private Integer highlightOrder;

    // Tags para recomendaciones
    @ManyToMany
    @JoinTable(
            name = "live_event_tags",
            joinColumns = @JoinColumn(name = "live_event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    // Instructores (reutiliza tu entidad existente Instructor)
    @ManyToMany
    @JoinTable(
            name = "live_event_instructors",
            joinColumns = @JoinColumn(name = "live_event_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> instructors;
}
