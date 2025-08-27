package com.aecode.webcoursesback.dtos.LiveANDShort.Home;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class ShortThumbDTO {
    private Long id;
    private String thumbnailUrl;
    private String videoUrl;
}
