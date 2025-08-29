package com.aecode.webcoursesback.dtos.Bot;
import com.aecode.webcoursesback.entities.Bot.Bot;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data @AllArgsConstructor @NoArgsConstructor
public class BotCreateUpdateDTO {
    private Bot.BotType type;            // INTERNAL | EXTERNAL
    private String title;
    private String ownerName;
    private String imageUrl;
    private String shortDescription;
    private String redirectUrl;
    private Bot.BotPlan plan;            // solo INTERNAL
    private BigDecimal price;        // solo PAID
    private String currency;         // solo PAID
    private boolean active;
    private Set<Integer> tagIds;     // ids de Tag
}
