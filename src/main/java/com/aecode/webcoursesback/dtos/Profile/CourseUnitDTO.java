package com.aecode.webcoursesback.dtos.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseUnitDTO {
    private Long moduleId;
    private String displayName;  // "Módulo 1" / "Paquete 2" / "Paquete Completo"
    private boolean hasAccess;
    private boolean completed;
}
