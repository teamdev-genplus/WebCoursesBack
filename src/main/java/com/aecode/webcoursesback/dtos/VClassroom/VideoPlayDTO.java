package com.aecode.webcoursesback.dtos.VClassroom;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoPlayDTO {
    private Long videoId;
    /** “Clase {orden} de {total} - {subtitle}” */
    private String headerTitle;

    private String sessionTitle;   // "Sesión 1"
    private String sessionLabel;   // "Introducción a BIM"
    private Integer orderNumber;

    private String videoUrl;
    private String description;    // resumen grande
    private String materialUrl;    // enlace único

    private boolean completed;
}