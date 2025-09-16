package com.aecode.webcoursesback.dtos.VClassroom;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoCardDTO {
    private Long videoId;
    private String sessionTitle;   // "Sesión 1"
    private String sessionLabel;   // "Introducción a BIM"
    private Integer orderNumber;
    private String thumbnailUrl;
    private boolean completed;     // para el check verde
}