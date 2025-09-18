package com.aecode.webcoursesback.dtos.VClassroom;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @Builder.Default
    private List<VideoResourceDTO> materials = new ArrayList<>();

    private boolean completed;
}