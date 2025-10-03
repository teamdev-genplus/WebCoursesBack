package com.aecode.webcoursesback.dtos.Landing;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LandingIndexDTO {
    private Long id;
    private String slug;
    private String title;      // tomado de principal[0].title si existe
    private String dateLabel;  // tomado de principal[0].dateLabel si existe
}