package com.aecode.webcoursesback.entities.Landing;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.time.OffsetDateTime;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "landing_pages")
public class LandingPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador lógico de la landing (ej: "ai-construction-summit-2025") */
    @Column(unique = true, length = 120)
    private String slug;

    /** ------ 1) Seccion Principal ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<PrincipalSection> principal;

    /** ------ 1) Colaboradores ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Collaborator> collaborators;

    /** ------ 2) Sección “Esto es AI construction Summit” ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private AboutSection about; // título, descripción, video principal y las 3+ cards de videos

    /** ------ 3) Speakers ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Speaker> speakers;

    /** ------ 4) Beneficios ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Benefit> benefits;

    /** ------ 5) Precios/Modalidades ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<PricingPlan> pricing;

    /** ------ 6) RRSS/Instagram ------ */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private SocialSection social;

    /** Auditoría simple */
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    /* ======== Tipos embebidos (mapeados como JSONB) ======== */

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PrincipalSection {
        private String title;        // "Google"
        private String description;
        private String dateLabel; //30 de noviembre  - Sonesta el Olivar
        private String backgroundimage;     // imagen
        private String downloadBrochure; //Enlace para descargar el brochure
        private String whattsappurl;
        //fecha para contador
        private OffsetDateTime startDate;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Collaborator {
        private String name;        // "Google"
        private String logoUrl;     // imagen
        private String websiteUrl;  // link opcional
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AboutSection {
        private String title;           // "Esto es AI Construction Summit"
        private String description;     // párrafo
        private String mainVideoUrl;    // video principal (embed/URL)
        private List<AboutCard> cards;  // cards con miniatura/título/desc
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AboutCard {
        private String title;
        private String description;
        private String thumbnailUrl; // imagen tipo “bombillo” del mock
        private String videoUrl;     // si se quiere abrir modal con ese video
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Speaker {
        private String fullName;     // “Bustani Lourdes…”
        private String headline;     // “UX-UI / Arquitecta…”
        private String photoUrl;     // imagen de fondo/card
        private String linkedinUrl;  // botón a LinkedIn
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BenefitSection {
        private String title;        // “Beneficios de Asistir”
        private String description;  // texto corto
        private List<Benefit> cards; //cards de los beneficios
    }


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Benefit {
        private String iconUrl;      // ícono (la “manzanita” del mock)
        private String title;        // “Networking de perfil”
        private String description;  // texto corto
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PricingPlan {
        /** Clave estable para el plan: p.ej. "regular", "comunidad", "corporativo" */
        private String key;
        private String title;              // “Corporativo”
        private String subtitle;           // breve (<= 6 palabras)
        private String currency;           // “USD” / “PEN”
        private String priceLabel;         // “$299.72 USD” (si prefieres formateado)
        private Double priceAmount;        // o como número si harán formato en front
        private Double promptPaymentPrice;      // precio pero con pago anticipado
        private Boolean promptPaymentEnabled; //bool que activa el pronto pago
        private String badgeText;          // “Cupos limitados”/“Pronto Pago”
        private String ctaText;            // “Comprar”
        private String ctaUrl;             // link de compra
        private String note;               // “Válido hasta el 19/10/25”, etc (opcional)

        /* ======== NUEVO: beneficios para la vista Inversión ======== */
        /** Párrafo entero que se muestra bajo "Antes del evento" */
        @Column(columnDefinition = "TEXT")
        private String beforeEventText;

        /** Párrafo entero que se muestra bajo "Durante el evento" */
        @Column(columnDefinition = "TEXT")
        private String duringEventText;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SocialSection {
        private String instagramHandle;  // “@aiconstructionsummit”
        private String moreUrl;          // “Ver más en Instagram”
        private List<SocialPost> posts;  // cuadritos con imagen/engagement
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SocialPost {
        private String imageUrl;
        private Integer likeCount;
        private Integer commentCount;
        private String linkUrl; // opcional para abrir post original
    }
}
