package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;
import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class AecobotCardDTO {
    private Long botId;
    private String imageUrl;
    private String title;
    private String developerName;
    private String planLabel;     // "FREE", "PAID", "BLOCKED"
    private BigDecimal price;     // solo si PAID
    private String currency;      // solo si PAID
    private boolean hasAccess;    // según clerkId
}
