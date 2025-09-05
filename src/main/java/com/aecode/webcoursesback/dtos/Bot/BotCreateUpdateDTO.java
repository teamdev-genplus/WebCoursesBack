package com.aecode.webcoursesback.dtos.Bot;
import com.aecode.webcoursesback.entities.Bot.Bot;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor
public class BotCreateUpdateDTO {
    private Bot.BotType type;        // INTERNAL | EXTERNAL
    private String title;
    private String subtitle;         // EXTERNAL
    private String ownerName;
    private String imageUrl;         // logo
    private String coverImageUrl;    // portada (EXTERNAL)
    private String shortDescription;
    private String redirectUrl;

    // INTERNAL
    private Bot.BotPlan plan;        // FREE | PAID | BLOCKED
    private BigDecimal price;        // solo PAID
    private String currency;         // solo PAID
    private String badge;            // texto libre (INTERNAL)

    private boolean active;

    // Orden/layout
    private Integer orderNumber;     // 0..N (menor = primero)
    private Boolean highlighted;     // EXTERNAL: candidato a destacado

    private Set<Long> categoryIds;
}
