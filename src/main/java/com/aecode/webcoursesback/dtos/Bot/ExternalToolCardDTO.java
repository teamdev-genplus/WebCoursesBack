package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;
import java.util.List;


@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ExternalToolCardDTO {
    private Long botId;
    private String coverImageUrl;     // portada grande
    private String logoImageUrl;      // imageUrl
    private String title;
    private List<String> categories;
    private String shortDescription;
    private boolean favorite;
    private String redirectUrl;       // requerido por UI
    private boolean highlighted;      // para layout
    private Integer orderNumber;      // para orden estable
}
