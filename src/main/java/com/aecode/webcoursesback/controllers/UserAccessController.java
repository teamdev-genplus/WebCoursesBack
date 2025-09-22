package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseResponseDTO;
import com.aecode.webcoursesback.dtos.Paid.UnifiedPaidOrderDTO;
import com.aecode.webcoursesback.dtos.Profile.ModuleProfileDTO;
import com.aecode.webcoursesback.entities.VClassroom.MarkVideoCompletedRequest;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.IUserAccessService;
import com.aecode.webcoursesback.services.Paid.PurchaseAccessService;

import com.aecode.webcoursesback.services.Paid.UnifiedPaidOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/user-access")
public class UserAccessController {

    @Autowired
    private IUserAccessService userAccessService;
    @Autowired
    private IUserProfileRepository userProfileRepo;
    @Autowired
    private IModuleRepo moduleRepo;
    @Autowired
    private EmailSenderService emailSenderService;

    // NUEVO: servicio que ya creaste en servicesimplement.Paid.PurchaseAccessServiceImpl
    @Autowired private PurchaseAccessService purchaseAccessService;

    // ... ya tienes varios @Autowired
    @Autowired private UnifiedPaidOrderService unifiedPaidOrderService;

    // ========= ADMIN: Compras unificadas =========
    @GetMapping("/admin/unified-purchases")
    public ResponseEntity<List<UnifiedPaidOrderDTO>> getUnifiedPurchases() {
        return ResponseEntity.ok(unifiedPaidOrderService.getAll());
    }
    /**
     * NUEVO: Otorga acceso por compra "front-asserted" (PayPal / Yape / Plin),
     * envía email HTML con los datos recibidos y persiste un recibo unificado.
     * Izipay se maneja aparte (IPN/validate).
     *
     * IMPORTANTE: Esto también guarda accesos en usermoduleaccess, porque
     * internamente llama a userAccessService.grantMultipleModuleAccess(...).
     */

    @PostMapping("/modules/access/purchase")
    public ResponseEntity<AccessPurchaseResponseDTO> grantModulesWithFrontAssertedPurchase(
            @Valid @RequestBody AccessPurchaseRequestDTO request
    ) {
        // Si ya usas el filtro de consistencia, aquí no necesitas más validación del clerkId.
        AccessPurchaseResponseDTO res = purchaseAccessService.processFrontAssertedPurchase(request);
        return ResponseEntity.ok(res);
    }


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
    @GetMapping("/courses/id/{courseId}/first-module")
    public ResponseEntity<ModuleProfileDTO> getFirstAccessibleModule(
            @RequestParam String clerkId,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(userAccessService.getFirstAccessibleModuleForUser(clerkId, courseId));
    }

    @GetMapping("/courses/{urlnamecourse}/first-module")
    public ResponseEntity<ModuleProfileDTO> getFirstAccessibleModuleByurlName(
            @RequestParam String clerkId,
            @PathVariable String urlnamecourse
    ) {
        return ResponseEntity.ok(
                userAccessService.getFirstAccessibleModuleForUserBySlug(clerkId, urlnamecourse)
        );
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
    public ResponseEntity<UserCourseDTO> grantCourseAccess(
            @RequestParam String clerkId,
            @RequestParam Long courseId
    ) {
        UserCourseDTO result = userAccessService.grantCourseAccess(clerkId, courseId);
        return ResponseEntity.ok(result);
    }

    /**
     * Otorgar acceso individual a un módulo al usuario.
     */
    @PostMapping("/modules/access")
    public ResponseEntity<UserModuleDTO> grantModuleAccess(
            @RequestParam String clerkId,
            @RequestParam Long moduleId
    ) {
        UserModuleDTO result = userAccessService.grantModuleAccess(clerkId, moduleId);
        return ResponseEntity.ok(result);
    }

    // ======================
    // TRACKING
    // ======================
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


    // ===== VER CONTENIDO DEL MÓDULO =====
// GET /user-access/modules/{moduleId}/content?clerkId=abc&videoId=123 (videoId opcional)
    @GetMapping("/modules/{moduleId}/content")
    public ResponseEntity<?> getModuleContent(
            @RequestParam String clerkId,
            @PathVariable Long moduleId,
            @RequestParam(required = false) Long videoId
    ) {
        return ResponseEntity.ok(userAccessService.getModuleContent(clerkId, moduleId, videoId));
    }

    // ===== MARCAR VIDEO COMO COMPLETADO =====
    // PUT /user-access/videos/{videoId}/complete?clerkId=abc   (body opcional: { "completed": true })
    @PutMapping("/videos/{videoId}/complete")
    public ResponseEntity<?> markVideoCompleted(
            @RequestParam String clerkId,
            @PathVariable Long videoId,
            @RequestBody(required = false) MarkVideoCompletedRequest body
    ) {
        Boolean completed = (body == null) ? Boolean.TRUE : body.getCompleted();
        return ResponseEntity.ok(userAccessService.markVideoCompleted(clerkId, videoId, completed));
    }



    // ======================
    // Helpers privados
    // ======================

    private static double nvl(Double d, double def) {
        return d == null ? def : d;
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;");
    }

    private static String nullSafe(String primary, String fallback) {
        return (primary == null || primary.isBlank()) ? fallback : primary;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
