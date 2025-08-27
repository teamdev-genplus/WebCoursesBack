package com.aecode.webcoursesback.dtos.LiveANDShort.Home;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LiveSimpleCardDTO {
    private Long liveId;
    private String title;
    private String generalCardImageUrl;
    private LocalDateTime startDateTime;

    //private boolean isPast;     // derivado
    //private boolean isUpcoming; // derivado
}
