package com.aecode.webcoursesback.entities.support;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "support_guide_pages", indexes = {
        @Index(name = "idx_support_guide_slug", columnList = "slug", unique = true)
})
public class SupportGuidePage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador lógico para rutear (p.ej. "soporte-general") */
    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    /** Cabecera de la vista */
    private String title;                 // "Video tutorial de Instrucciones"
    @Column(columnDefinition = "TEXT")
    private String intro;                 // texto largo opcional

    /** Lista de videos + FAQs (todo JSONB) */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<VideoItem> videos;

    /** Auditoría (UTC) */
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now; updatedAt = now;
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    /* ======= Embebidos JSONB ======= */

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VideoItem {
        private String key;           // estable: "configuracion-inicial"
        private Integer position;     // orden ASC para cards e inicial
        private String title;
        @Column(columnDefinition = "TEXT")
        private String description;
        private String thumbnailUrl;  // imagen en la card
        private String videoUrl;      // url/embed del video
        private String durationLabel; // "09:25"
        private List<FaqItem> faqs;   // FAQs por video
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FaqItem {
        private String question;
        @Column(columnDefinition = "TEXT")
        private String answer;
    }
}
