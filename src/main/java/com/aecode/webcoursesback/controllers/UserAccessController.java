package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.UserCourseAccess;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import com.aecode.webcoursesback.services.IUserAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-access")
public class UserAccessController {

    @Autowired
    private IUserAccessService userAccessService;

    // Obtener cards de cursos accesibles para usuario
    @GetMapping("/courses/{userId}")
    public ResponseEntity<List<CourseCardProgressDTO>> getUserCourses(@PathVariable Long userId) {
        List<CourseCardProgressDTO> courses = userAccessService.getAccessibleCoursesForUser(userId);
        return ResponseEntity.ok(courses);
    }

    //obtener el primer módulo comprado de un curso por usuario
    @GetMapping("/first-module/{userId}/{courseId}")
    public ResponseEntity<ModuleDTO> getFirstAccessibleModule(@PathVariable Long userId, @PathVariable Long courseId) {
        ModuleDTO firstModule = userAccessService.getFirstAccessibleModuleForUser(userId, courseId);
        if (firstModule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(firstModule);
    }

    // Validar acceso a módulo
    @GetMapping("/module/{userId}/{moduleId}")
    public ResponseEntity<?> getModuleIfHasAccess(@PathVariable Long userId, @PathVariable Long moduleId) {
        boolean hasAccess = userAccessService.hasAccessToModule(userId, moduleId);
        if (!hasAccess) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No tienes acceso a este módulo");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        ModuleDTO moduleDTO = userAccessService.getModuleById(moduleId);
        return ResponseEntity.ok(moduleDTO);
    }

    //Listar todo para curso
    @GetMapping("/all-courses")
    public ResponseEntity<List<UserCourseDTO>> getAll() {
        List<UserCourseDTO> accesses = userAccessService.getAllCourses();
        return ResponseEntity.ok(accesses);
    }
    //Listar todo para modulo
    @GetMapping("/all-modules")
    public ResponseEntity<List<UserModuleDTO>> getAllModules() {
        List<UserModuleDTO> accesses = userAccessService.getAllModules();
        return ResponseEntity.ok(accesses);
    }

    // Listar módulos accesibles por usuario
    @GetMapping("/modules/{userId}")
    public ResponseEntity<List<UserModuleDTO>> getUserModules(@PathVariable Long userId) {
        List<UserModuleDTO> modules = userAccessService.getUserModulesByUserId(userId);
        return ResponseEntity.ok(modules);
    }


    // Registrar acceso a curso (compra completa)
    @PostMapping("/grant-course")
    public ResponseEntity<UserCourseAccess> grantCourseAccess(@RequestParam Long userId, @RequestParam Long courseId) {
        UserCourseAccess access = userAccessService.grantCourseAccess(userId, courseId);
        return ResponseEntity.ok(access);
    }

    // Para compras individuales
    @PostMapping("/grant-module")
    public ResponseEntity<UserModuleAccess> grantModuleAccess(@RequestParam Long userId, @RequestParam Long moduleId) {
        UserModuleAccess access = userAccessService.grantModuleAccess(userId, moduleId);
        return ResponseEntity.ok(access);
    }

    // Para compras múltiples
    @PostMapping("/grant-modules")
    public ResponseEntity<List<UserModuleAccess>> grantMultipleModules(
            @RequestParam Long userId,
            @RequestBody List<Long> moduleIds) {
        List<UserModuleAccess> accesses = userAccessService.grantMultipleModuleAccess(userId, moduleIds);
        return ResponseEntity.ok(accesses);
    }


    // marcar módulo como completado
    @PutMapping("/complete-module")
    public ResponseEntity<?> markModuleAsCompleted(@RequestParam Long userId, @RequestParam Long moduleId) {
        boolean updated = userAccessService.markModuleAsCompleted(userId, moduleId);
        if (updated) {
            return ResponseEntity.ok("Módulo marcado como completado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acceso al módulo no encontrado");
        }
    }



}
