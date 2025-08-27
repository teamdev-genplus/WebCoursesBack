package com.aecode.webcoursesback.dtos.LiveANDShort.Home;
import lombok.*;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class LiveHomeDTO {
    private List<LiveFeaturedCardDTO> featuredLives;
    private List<LiveSimpleCardDTO> upcomingLives;
    private List<LiveSimpleCardDTO> pastLives;
    private List<ShortThumbDTO> shorts; // solo portada + link
}
