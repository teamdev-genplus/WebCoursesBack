package com.aecode.webcoursesback.dtos.Profile;
import com.aecode.webcoursesback.dtos.*;
import jakarta.persistence.Column;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleProfileDTO {
    private Long moduleId;
    private Long courseId;

    private String titleStudyplan;
    private String courseTypeLabel;

    @Builder.Default
    private List<StudyPlanDTO> studyPlans = new ArrayList<>();
    private Integer orderNumber;

    // NUEVO: LOGOS de herramientas relacionadas al módulo (solo imagen, sin nombre)
    @Builder.Default
    private List<String> toolPictures = new ArrayList<>();

    //DETALLES DEL MÓDULO
    private String whatsappGroupLink;
    // Enlaces de herramientas (dropdown)
    @Builder.Default
    private List<ToolLinkDTO> tools = new ArrayList<>();

    // NUEVOS BOTONES (migrados a nivel de módulo)
    private String urlrecording;
    private String viewpresentation;

    // Modo y comportamiento de horarios
    private String mode;        // "ASINCRONO", "ENVIVO", "PROXIMO", "GRATUITO", "MIXTO"
    private boolean isLive;     // true si ENVIVO
    private boolean available247; // true si NO es ENVIVO  => "Disponible 24/7"

    // Horarios (solo si isLive == true)
    @Builder.Default
    private List<ScheduleDTO> schedules = new ArrayList<>();
    private String urlJoinClass; // solo live

    //CERTIFICADOS
    private List<MyCertificateDTO> certificates;

    //lISTA DE MODULOS
    @Builder.Default
    private List<ModuleAccessDTO> courseModules= new ArrayList<>();

    //campos solo para mode "EXCLUSIVO"
    private String urlimagelogo1;
    private String urlimagelogo2;

}
