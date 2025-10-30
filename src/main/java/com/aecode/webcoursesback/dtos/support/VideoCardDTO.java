package com.aecode.webcoursesback.dtos.support;
import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoCardDTO {
    private String key;
    private String title;
    private String thumbnailUrl;
    private String durationLabel;
    private Integer position;
}
