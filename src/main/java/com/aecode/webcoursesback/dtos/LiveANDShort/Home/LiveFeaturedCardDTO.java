package com.aecode.webcoursesback.dtos.LiveANDShort.Home;
import lombok.*;
import java.time.LocalDateTime;


@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LiveFeaturedCardDTO {
    private Long id;
    private String title;
    private String featuredCardDescription;
    private String featuredImageUrl; // misma para detalle
    private LocalDateTime startDateTime; // front formatea "Sab 06 SET" y hora
}
