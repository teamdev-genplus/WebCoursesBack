package com.aecode.webcoursesback.dtos.Profile;
import com.aecode.webcoursesback.dtos.ModuleListDTO;
import com.aecode.webcoursesback.dtos.MyCertificateDTO;
import com.aecode.webcoursesback.dtos.ScheduleDTO;
import com.aecode.webcoursesback.dtos.StudyPlanDTO;
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

    //DETALLES DEL MÓDULO
    private String whatsappGroupLink;

    // Enlaces de herramientas (dropdown)
    @Builder.Default
    private List<ToolLinkDTO> tools = new ArrayList<>();

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
}
