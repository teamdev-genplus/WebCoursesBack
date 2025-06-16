package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.dtos.UserCourseDTO;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
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
    public ResponseEntity<List<CourseCardDTO>> getUserCourses(@PathVariable Long userId) {
        List<CourseCardDTO> courses = userAccessService.getAccessibleCoursesForUser(userId);
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

    // Registrar acceso a curso (compra completa)
    @PostMapping("/grant-course")
    public ResponseEntity<UserCourseAccess> grantCourseAccess(@RequestParam Long userId, @RequestParam Long courseId) {
        UserCourseAccess access = userAccessService.grantCourseAccess(userId, courseId);
        return ResponseEntity.ok(access);
    }

    // Registrar acceso a módulo (compra individual)
    @PostMapping("/grant-module")
    public ResponseEntity<UserModuleAccess> grantModuleAccess(@RequestParam Long userId, @RequestParam Long moduleId) {
        UserModuleAccess access = userAccessService.grantModuleAccess(userId, moduleId);
        return ResponseEntity.ok(access);
    }
}
