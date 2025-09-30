package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseModuleViewDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.services.IModuleService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/modules")
public class ModuleController {
    @Autowired
    private IModuleService mS;

    private final ModelMapper modelMapper = new ModelMapper();

    //----------------------------------------------------------ASISTENTEKEY----------------------------------------------------------------
    @PatchMapping("/{moduleId}/assistant-key")
    public ResponseEntity<Void> setAssistantKey(
            @PathVariable Long moduleId,
            @RequestBody SetAssistantKeyRequest req
    ) {
        // req.key puede venir null/blank para limpiar
        mS.setAssistantApiKey(moduleId, req.getKey());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{moduleId}/assistant-key/masked")
    public ResponseEntity<String> getAssistantKeyMasked(@PathVariable Long moduleId) {
        String masked = mS.getAssistantApiKeyMasked(moduleId);
        return ResponseEntity.ok(masked == null ? "" : masked);
    }

    @Data
    public static class SetAssistantKeyRequest {
        private String key; // raw; se encripta en el servicio
    }
    //----------------------------------------------------------GET----------------------------------------------------------------

    @GetMapping("/listall")
    public ResponseEntity<List<ModuleDTO>> listAll() {
        List<Module> modules = mS.listAll();
        List<ModuleDTO> modulesDTO = modules.stream()
                .map(module -> modelMapper.map(module, ModuleDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(modulesDTO, HttpStatus.OK);
    }

    //Entregar el primer modulo
    @GetMapping("/course-and-first-module/{courseId}")
    public ResponseEntity<CourseModuleViewDTO> getFirstModuleByCourseId(@PathVariable Long courseId) {
        CourseModuleViewDTO dto  = mS.getCourseAndFirstModule(courseId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/detail/{moduleId}")
    public ResponseEntity<ModuleDTO> getModuleDetail(@PathVariable Long moduleId) {
        ModuleDTO moduleDTO = mS.getModuleDetailById(moduleId);
        return new ResponseEntity<>(moduleDTO, HttpStatus.OK);
    }
    //----------------------------------------------------------DELETE----------------------------------------------------------------

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> delete(@PathVariable Long moduleId) {
        mS.delete(moduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //----------------------------------------------------------EXCEPTIONS----------------------------------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
