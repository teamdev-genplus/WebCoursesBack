package com.aecode.webcoursesback.dtos.Bot;
import com.aecode.webcoursesback.entities.Bot.Bot;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class BotCreateUpdateDTO {
    private Bot.BotType type;        // INTERNAL | EXTERNAL
    private String title;
    private String ownerName;
    private String imageUrl;
    private String shortDescription;
    private String redirectUrl;

    private Bot.BotPlan plan;        // solo INTERNAL
    private java.math.BigDecimal price; // solo PAID
    private String currency;         // solo PAID
    private boolean active;

    private String badge;      // etiqueta UI (solo INTERNAL), opcional

    private java.util.Set<Long> categoryIds; // NUEVO
}
