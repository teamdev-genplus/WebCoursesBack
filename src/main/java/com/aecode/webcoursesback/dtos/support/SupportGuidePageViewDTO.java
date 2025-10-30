package com.aecode.webcoursesback.dtos.support;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

/** Vista pública “page”: cards + header + initialVideo */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupportGuidePageViewDTO {
    private Long id;
    private String slug;
    private String title;
    private String intro;
    private List<VideoCardDTO> cards;    // columna derecha
    private VideoDetailDTO initialVideo; // video inicial (por key o por position)
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}