package com.aecode.webcoursesback.dtos.Profile;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ToolLinkDTO {
    private String name; // nombre visible del recurso (ej. "Miro", "Discord", "Roadmap")
    private String url;// enlace correspondiente
}
