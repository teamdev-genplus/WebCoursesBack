package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.ModuleProfileDTO;
import com.aecode.webcoursesback.entities.UserCourseAccess;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import com.aecode.webcoursesback.services.IUserAccessService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/user-access")
public class UserAccessController {

    @Autowired
    private IUserAccessService userAccessService;

    // ======================
    // ACCESO DEL USUARIO
    // ======================

    /**
     * Obtener cards de los cursos a los que el usuario tiene acceso (completo o parcial).
     */
    @GetMapping("/courses/{clerkId}")
    public ResponseEntity<List<CourseCardProgressDTO>> getUserCourses(@PathVariable String clerkId) {
        return ResponseEntity.ok(userAccessService.getAccessibleCoursesForUser(clerkId));
    }

    /**
     * Obtener el primer módulo disponible de un curso al que el usuario tenga acceso.
     */
    @GetMapping("/courses/{courseId}/first-module")
    public ResponseEntity<ModuleProfileDTO> getFirstAccessibleModule(
            @RequestParam String clerkId,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(userAccessService.getFirstAccessibleModuleForUser(clerkId, courseId));
    }

    /**
     * Obtener información de un módulo si el usuario tiene acceso a él.
     */
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<?> getModuleIfHasAccess(
            @RequestParam String clerkId,
            @PathVariable Long moduleId
    ) {
        return ResponseEntity.ok(userAccessService.getModuleById(moduleId, clerkId));
    }

    /**
     * Obtener los módulos a los que el usuario tiene acceso.
     */
    @GetMapping("/modules")
    public ResponseEntity<List<UserModuleDTO>> getUserModules(@RequestParam String clerkId) {
        return ResponseEntity.ok(userAccessService.getUserModulesByClerkId(clerkId));
    }

    // ======================
    // ACCESO ADMIN / BACKOFFICE
    // ======================

    /**
     * Obtener todos los cursos con acceso registrado.
     */
    @GetMapping("/admin/courses")
    public ResponseEntity<List<UserCourseDTO>> getAllCoursesAccess() {
        return ResponseEntity.ok(userAccessService.getAllCourses());
    }

    /**
     * Obtener todos los módulos con acceso registrado.
     */
    @GetMapping("/admin/modules")
    public ResponseEntity<List<UserModuleDTO>> getAllModulesAccess() {
        return ResponseEntity.ok(userAccessService.getAllModules());
    }

    // ======================
    // GESTIÓN DE ACCESO (COMPRAS / TRACKING)
    // ======================

    /**
     * Otorgar acceso completo a un curso al usuario (incluye todos los módulos).
     */
    @PostMapping("/courses/access")
    public ResponseEntity<UserCourseAccess> grantCourseAccess(
            @RequestParam String clerkId,
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(userAccessService.grantCourseAccess(clerkId, courseId));
    }

    /**
     * Otorgar acceso individual a un módulo al usuario.
     */
    @PostMapping("/modules/access")
    public ResponseEntity<UserModuleAccess> grantModuleAccess(
            @RequestParam String clerkId,
            @RequestParam Long moduleId
    ) {
        return ResponseEntity.ok(userAccessService.grantModuleAccess(clerkId, moduleId));
    }

    /**
     * Otorgar acceso a múltiples módulos al usuario (compra parcial).
     */
    @PostMapping("/modules/access/multiple")
    public ResponseEntity<List<UserModuleAccess>> grantMultipleModules(
            @RequestParam String clerkId,
            @RequestBody List<Long> moduleIds
    ) {
        return ResponseEntity.ok(userAccessService.grantMultipleModuleAccess(clerkId, moduleIds));
    }

    /**
     * Marcar un módulo como completado por el usuario.
     */
    @PutMapping("/modules/{moduleId}/complete")
    public ResponseEntity<?> markModuleAsCompleted(
            @RequestParam String clerkId,
            @PathVariable Long moduleId
    ) {
        boolean updated = userAccessService.markModuleAsCompleted(clerkId, moduleId);
        return updated
                ? ResponseEntity.ok("Módulo marcado como completado")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acceso al módulo no encontrado");
    }
}
