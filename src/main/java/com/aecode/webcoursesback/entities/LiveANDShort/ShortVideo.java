package com.aecode.webcoursesback.entities.LiveANDShort;
import com.aecode.webcoursesback.entities.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "short_videos")
@SequenceGenerator(name = "short_video_seq", sequenceName = "short_video_sequence", allocationSize = 1)
public class ShortVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "short_video_seq")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title; // no aparece en Home, sí en recomendaciones del detalle

    @Column(length = 500)
    private String shortDescription; // ~3 líneas para recomendaciones

    @Column(length = 1000)
    private String thumbnailUrl; // portada/imagen del corto (Home y recomendaciones)

    @Column(length = 1000)
    private String videoUrl; // enlace al video (YouTube, TikTok, etc.)

    private boolean active = true;

    private LocalDateTime publishedAt; // opcional

    @ManyToMany
    @JoinTable(
            name = "short_video_tags",
            joinColumns = @JoinColumn(name = "short_video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;
}
