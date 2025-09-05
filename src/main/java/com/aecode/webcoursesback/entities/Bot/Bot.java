package com.aecode.webcoursesback.entities.Bot;
import com.aecode.webcoursesback.entities.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bots")
@SequenceGenerator(name = "bot_seq", sequenceName = "bot_sequence", allocationSize = 1)
public class Bot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_seq")
    private Long botId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BotType type; // INTERNAL | EXTERNAL

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String subtitle;

    // Nombre del creador (INTERNAL) o proveedor (EXTERNAL)
    @Column(nullable = false, length = 255)
    private String ownerName;

    // Imagen/logo del card
    @Column(length = 1000)
    private String imageUrl;

    // Portada (principalmente para EXTERNAL)
    @Column(length = 1000)
    private String coverImageUrl;

    // Descripción corta opcional (para tooltips o futuras vistas)
    @Column(length = 500)
    private String shortDescription;

    // URL de redirección:
    // - INTERNAL: puede ser una ruta interna (e.g. /aecobots/{slug})
    // - EXTERNAL: URL absoluta a la herramienta externa
    @Column(length = 1000)
    private String redirectUrl;

    // Solo aplica a INTERNAL
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BotPlan plan; // FREE | PAID | BLOCKED (nullable para EXTERNAL)

    // Solo aplica a INTERNAL y plan = PAID
    @Column(precision = 12, scale = 2)
    private BigDecimal price; // nullable

    @Column(length = 10)
    private String currency; // nullable (e.g., "PEN", "USD")

    // Control de visibilidad
    @Column(nullable = false)
    private boolean active = true;

    // NUEVO: badge superior izquierda (solo se muestra en INTERNAL)
    private String badge; // FREE, PREMIUM, PROXIMAMENTE, EXCLUSIVO_REVIT, EXCLUSIVO_DYNAMO, etc.

    // Orden y destacado (para layout de AI Tools y orden general)
    @Column()
    private Integer orderNumber = 0;

    @Column()
    private boolean highlighted = false;

    // NUEVO: categorías (reutilizable para otros dominios)
    @ManyToMany
    @JoinTable(
            name = "bot_categories",
            joinColumns = @JoinColumn(name = "bot_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;


    public enum BotType {
        INTERNAL, // AECObots de la empresa (free/paid/blocked)
        EXTERNAL  // AI TOOLS externos (solo redirección)
    }
    public enum BotPlan {
        FREE,
        PAID,
        BLOCKED // visible pero no disponible aún
    }
}
