package com.aecode.webcoursesback.dtos.LiveANDShort.Detail;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class LiveRecommendationDTO {
    private Long id;
    private String title;
    private String generalCardImageUrl;
    private LocalDateTime startDateTime;
}
