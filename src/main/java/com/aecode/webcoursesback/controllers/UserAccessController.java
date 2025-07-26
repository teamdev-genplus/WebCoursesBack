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
    @GetMapping("/courses/{clerkId}")
    public ResponseEntity<List<CourseCardProgressDTO>> getUserCourses(@PathVariable String clerkId) {
        List<CourseCardProgressDTO> courses = userAccessService.getAccessibleCoursesForUser(clerkId);
        return ResponseEntity.ok(courses);
    }

    // Obtener el primer módulo comprado de un curso por usuario
    @GetMapping("/first-module/{clerkId}/{courseId}")
    public ResponseEntity<ModuleDTO> getFirstAccessibleModule(@PathVariable String clerkId, @PathVariable Long courseId) {
        ModuleDTO firstModule = userAccessService.getFirstAccessibleModuleForUser(clerkId, courseId);
        if (firstModule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(firstModule);
    }

    // Validar acceso a módulo
    @GetMapping("/module/{clerkId}/{moduleId}")
    public ResponseEntity<?> getModuleIfHasAccess(@PathVariable String clerkId, @PathVariable Long moduleId) {
        boolean hasAccess = userAccessService.hasAccessToModule(clerkId, moduleId);
        if (!hasAccess) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No tienes acceso a este módulo");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        ModuleDTO moduleDTO = userAccessService.getModuleById(moduleId);
        return ResponseEntity.ok(moduleDTO);
    }

    // Listar todo para curso
    @GetMapping("/all-courses")
    public ResponseEntity<List<UserCourseDTO>> getAll() {
        List<UserCourseDTO> accesses = userAccessService.getAllCourses();
        return ResponseEntity.ok(accesses);
    }

    // Listar todo para módulo
    @GetMapping("/all-modules")
    public ResponseEntity<List<UserModuleDTO>> getAllModules() {
        List<UserModuleDTO> accesses = userAccessService.getAllModules();
        return ResponseEntity.ok(accesses);
    }

    // Listar módulos accesibles por usuario
    @GetMapping("/modules/{clerkId}")
    public ResponseEntity<List<UserModuleDTO>> getUserModules(@PathVariable String clerkId) {
        List<UserModuleDTO> modules = userAccessService.getUserModulesByClerkId(clerkId);
        return ResponseEntity.ok(modules);
    }

    // Registrar acceso a curso (compra completa)
    @PostMapping("/grant-course")
    public ResponseEntity<UserCourseAccess> grantCourseAccess(@RequestParam String clerkId, @RequestParam Long courseId) {
        UserCourseAccess access = userAccessService.grantCourseAccess(clerkId, courseId);
        return ResponseEntity.ok(access);
    }

    // Registrar acceso a módulo individual
    @PostMapping("/grant-module")
    public ResponseEntity<UserModuleAccess> grantModuleAccess(@RequestParam String clerkId, @RequestParam Long moduleId) {
        UserModuleAccess access = userAccessService.grantModuleAccess(clerkId, moduleId);
        return ResponseEntity.ok(access);
    }

    // Registrar acceso a múltiples módulos
    @PostMapping("/grant-modules")
    public ResponseEntity<List<UserModuleAccess>> grantMultipleModules(
            @RequestParam String clerkId,
            @RequestBody List<Long> moduleIds) {
        List<UserModuleAccess> accesses = userAccessService.grantMultipleModuleAccess(clerkId, moduleIds);
        return ResponseEntity.ok(accesses);
    }

    // Marcar módulo como completado
    @PutMapping("/complete-module")
    public ResponseEntity<?> markModuleAsCompleted(@RequestParam String clerkId, @RequestParam Long moduleId) {
        boolean updated = userAccessService.markModuleAsCompleted(clerkId, moduleId);
        if (updated) {
            return ResponseEntity.ok("Módulo marcado como completado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acceso al módulo no encontrado");
        }
    }

}
