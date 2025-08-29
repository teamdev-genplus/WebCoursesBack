package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class ExternalToolCardDTO {
    private Long botId;
    private String imageUrl;
    private String title;
    private String providerName;
    private List<String> tagNames; // visible en card (tipo de bot), proviene de Tag
}
