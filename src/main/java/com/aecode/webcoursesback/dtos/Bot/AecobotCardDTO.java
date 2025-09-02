package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;


@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AecobotCardDTO {
    private Long botId;
    private String imageUrl;          // puede ser la imagen principal
    private String title;
    private List<String> categories;
    private String shortDescription;  // 2 líneas máx en UI
    private String ownerName;         // "By pepito"
    private boolean favorite;         // corazón
    private String badge;             // texto libre (solo INTERNAL)
    private BigDecimal price;         // solo plan=PAID
    private String currency;
    private Boolean hasAccess;        // opcional
    private String redirectUrl;       // requerido por UI
    private Integer orderNumber;      // para orden estable
}
