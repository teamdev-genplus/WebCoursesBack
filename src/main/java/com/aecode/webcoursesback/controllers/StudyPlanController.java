package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.StudyPlanDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.entities.StudyPlan;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.services.ISecondCourseService;
import com.aecode.webcoursesback.services.IStudyPlanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/studyplan")
public class StudyPlanController {
    @Autowired
    private IStudyPlanService spS;
    @Autowired
    private IModuleService mS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody StudyPlanDTO dto) {

        Module module = mS.listId(dto.getModuleId());

        if (module == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Modulo no encontrado");
        }
        // Mapear el DTO a la entidad
        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setSessions(dto.getSessions());
        studyPlan.setHours(dto.getHours());
        studyPlan.setUnit(dto.getUnit());
        studyPlan.setOrderNumber(dto.getOrderNumber());

        spS.insert(studyPlan);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<StudyPlanDTO> list() {
        ModelMapper m = new ModelMapper();
        List<StudyPlan> a = spS.list();
        return a.stream().map(module -> {

            StudyPlanDTO dto = m.map(module, StudyPlanDTO.class);
            dto.setModuleId(module.getModule().getModuleId());
            return dto;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        spS.delete(id);
    }

    @GetMapping("/{id}")
    public StudyPlanDTO listId(@PathVariable("id") Integer id) {
        ModelMapper m = new ModelMapper();
        StudyPlan module = spS.listId(id);

        StudyPlanDTO dto = m.map(module, StudyPlanDTO.class);
        dto.setModuleId(module.getModule().getModuleId());

        return dto;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody StudyPlanDTO studyPlanDTO) {
        try {
            // Obtener el curso existente por ID
            StudyPlan existingStudyPlan = spS.listId(id);
            if (studyPlanDTO.getUnit() != null) {
                existingStudyPlan.setUnit(studyPlanDTO.getUnit());
            }
            if (studyPlanDTO.getSessions() != null) {
                existingStudyPlan.setSessions(studyPlanDTO.getSessions());
            }
            if (studyPlanDTO.getHours() != 0) {
                existingStudyPlan.setHours(studyPlanDTO.getHours());
            }
            if (studyPlanDTO.getOrderNumber() != 0) {
                existingStudyPlan.setOrderNumber(studyPlanDTO.getOrderNumber());
            }
            if (studyPlanDTO.getModuleId() != 0) {
                // Relacionar el módulo con otro curso si el curso ID es diferente
                Module module = mS.listId(studyPlanDTO.getModuleId());
                if (module == null || module.getModuleId() == 0) {
                    return ResponseEntity.status(404).body("MicroCurso asociado no encontrado");
                }
                existingStudyPlan.setModule(module);
            }

            // Guardar los cambios
            spS.insert(existingStudyPlan);

            return ResponseEntity.ok("Plan de estudio actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el Plan de estudio: " + e.getMessage());
        }
    }
}
