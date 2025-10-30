package com.aecode.webcoursesback.dtos.support;
import lombok.*;

/** Índice público para descubrir slugs */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupportGuideIndexDTO {
    private Long id;
    private String slug;
    private String title;
}