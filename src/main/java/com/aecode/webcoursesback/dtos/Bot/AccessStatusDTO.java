package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class AccessStatusDTO {
    private Long botId;
    private boolean hasAccess;
}
