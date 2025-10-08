package com.aecode.webcoursesback.controllers.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.dtos.Landing.Inversion.LandingInvestmentDTO;
import com.aecode.webcoursesback.services.Landing.LandingPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/landing")
public class LandingPageController {

    private final LandingPageService service;

    /* ==================== PÚBLICO (Front) ==================== */

    /** Descubrimiento público: lista de landings con slug para enrutar */
    @GetMapping("/public-index")
    public ResponseEntity<List<LandingIndexDTO>> publicIndex() {
        return ResponseEntity.ok(service.listPublicIndex());
    }

    /** GET público: trae toda la landing por slug */
    @GetMapping("/{slug}")
    public ResponseEntity<LandingPageDTO> get(@PathVariable String slug) {
        return ResponseEntity.ok(service.getBySlug(slug));
    }


    /** NUEVO — Vista "Inversión": titles + párrafos de beneficios + importes */
    @GetMapping("/{slug}/investment")
    public ResponseEntity<LandingInvestmentDTO> getInvestment(
            @PathVariable String slug,
            @RequestParam(required = false) String planKey,          // ej: "regular"
            @RequestParam(required = false) Double taxRate,          // ej: 0.18
            @RequestParam(required = false) Boolean priceIncludesTax // default false
    ) {
        return ResponseEntity.ok(service.getInvestmentDetail(slug, planKey, taxRate, priceIncludesTax));
    }

    /* ==================== ADMIN ==================== */

    /** Índice admin: lista con id + slug */
    @GetMapping("/admin/index")
    public ResponseEntity<List<LandingIndexDTO>> adminIndex() {
        return ResponseEntity.ok(service.listAdminIndex());
    }

    /** POST (admin): crea o reemplaza todo el contenido de la landing (sigue recibiendo slug en el DTO) */
    @PostMapping("/admin")
    public ResponseEntity<LandingPageDTO> upsert(@RequestBody LandingPageDTO dto) {
        return ResponseEntity.ok(service.upsert(dto));
    }

    /** PATCH (admin): Sección Principal por ID */
    @PatchMapping("/admin/{id}/principal")
    public ResponseEntity<LandingPageDTO> patchPrincipal(
            @PathVariable Long id, @RequestBody UpdatePrincipalDTO dto) {
        return ResponseEntity.ok(service.patchPrincipalById(id, dto));
    }

    /** PATCH (admin): Colaboradores por ID */
    @PatchMapping("/admin/{id}/collaborators")
    public ResponseEntity<LandingPageDTO> patchCollaborators(
            @PathVariable Long id, @RequestBody UpdateCollaboratorsDTO dto) {
        return ResponseEntity.ok(service.patchCollaboratorsById(id, dto));
    }

    /** PATCH (admin): About por ID */
    @PatchMapping("/admin/{id}/about")
    public ResponseEntity<LandingPageDTO> patchAbout(
            @PathVariable Long id, @RequestBody UpdateAboutDTO dto) {
        return ResponseEntity.ok(service.patchAboutById(id, dto));
    }

    /** PATCH (admin): Speakers por ID */
    @PatchMapping("/admin/{id}/speakers")
    public ResponseEntity<LandingPageDTO> patchSpeakers(
            @PathVariable Long id, @RequestBody UpdateSpeakersDTO dto) {
        return ResponseEntity.ok(service.patchSpeakersById(id, dto));
    }

    /** PATCH (admin): Beneficios por ID */
    @PatchMapping("/admin/{id}/benefits")
    public ResponseEntity<LandingPageDTO> patchBenefits(
            @PathVariable Long id, @RequestBody UpdateBenefitsDTO dto) {
        return ResponseEntity.ok(service.patchBenefitsById(id, dto));
    }

    @PatchMapping("/admin/{id}/pricing")
    public ResponseEntity<LandingPageDTO> patchPricing(
            @PathVariable Long id, @RequestBody UpdatePricingDTO dto) {
        return ResponseEntity.ok(service.patchPricingById(id, dto));
    }

    @PatchMapping("/admin/{id}/social")
    public ResponseEntity<LandingPageDTO> patchSocial(
            @PathVariable Long id, @RequestBody UpdateSocialDTO dto) {
        return ResponseEntity.ok(service.patchSocialById(id, dto));
    }

}