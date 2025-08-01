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
    private String programTitle;
    private String textmode;
    private String description;
    @Builder.Default
    private List<StudyPlanDTO> studyPlans = new ArrayList<>();
    private Integer orderNumber;

    //DETALLES DEL CURSO
    private String whatsappGroupLink;
    private String dmaterial;

    //Horarios
    @Builder.Default
    private List<ScheduleDTO> schedules = new ArrayList<>();
    private String urlJoinClass;

    //CERTIFICADOS
    private List<MyCertificateDTO> certificates;

    //lISTA DE MODULOS
    @Builder.Default
    private List<ModuleAccessDTO> courseModules= new ArrayList<>();
}
