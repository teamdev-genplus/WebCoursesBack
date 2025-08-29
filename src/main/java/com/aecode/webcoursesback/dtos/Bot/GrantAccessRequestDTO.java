package com.aecode.webcoursesback.dtos.Bot;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class GrantAccessRequestDTO {
    private String clerkId;
    private Long botId;
    private String source; // "FREE", "PURCHASE", "ADMIN"
}
