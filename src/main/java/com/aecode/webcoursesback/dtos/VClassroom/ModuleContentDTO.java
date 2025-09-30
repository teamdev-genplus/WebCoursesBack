package com.aecode.webcoursesback.dtos.VClassroom;
import lombok.*;
import java.util.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuleContentDTO {
    private Long moduleId;
    private Long courseId;
    private String moduleTitle;

    private Integer totalVideos;
    private VideoPlayDTO current;           // video que se muestra en el player
    @Builder.Default
    private List<VideoCardDTO> playlist = new ArrayList<>(); // cards del lateral

    // === NUEVO: clave desencriptada para el front (solo si el usuario tiene acceso) ===
    private String assistantApiKey;
}
