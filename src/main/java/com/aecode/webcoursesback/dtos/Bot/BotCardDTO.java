package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BotCardDTO {
    private Long botId;
    private String imageUrl;
    private String title;
    private List<String> categories;      // nombres de categorías
    private String shortDescription;      // 2 líneas máx en UI
    private String ownerName;             // "By pepito"
    private boolean favorite;             // corazón superior derecho
    private String badge;                 // "Free", "Premium", "Próximamente", "Exclusivo para Revit" (solo INTERNAL)
    private BigDecimal price;             // solo INTERNAL y plan=PAID
    private String currency;              // idem
    private Boolean hasAccess;            // opcional (solo INTERNAL; si no lo usas en UI, puedes ignorarlo)
}
