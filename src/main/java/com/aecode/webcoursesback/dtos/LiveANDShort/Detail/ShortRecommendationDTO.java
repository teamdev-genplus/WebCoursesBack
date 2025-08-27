package com.aecode.webcoursesback.dtos.LiveANDShort.Detail;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class ShortRecommendationDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String thumbnailUrl;
    private String videoUrl;
}
