package com.aecode.webcoursesback.controllers.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.dtos.Landing.Inversion.*;
import com.aecode.webcoursesback.dtos.Landing.Participantes.ParticipantGroupListResponse;
import com.aecode.webcoursesback.dtos.Landing.Solicitud.CallForPresentationSubmissionDTO;
import com.aecode.webcoursesback.dtos.Landing.Solicitud.SubmitCallForPresentationDTO;
import com.aecode.webcoursesback.dtos.Paid.Event.AccessEventPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.Event.AccessEventPurchaseResponseDTO;
import com.aecode.webcoursesback.services.Landing.LandingPageService;
import com.aecode.webcoursesback.services.Paid.PurchaseAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/landing")
public class LandingPageController {

    private final LandingPageService service;
    private final PurchaseAccessService purchaseAccessService;

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
            @RequestParam(required = false) String modality,
            @RequestParam(required = false) String planKey,   // "aecoder" | "corporativo" | "general" (case-insensitive)
            @RequestParam(required = false) Integer quantity, // null -> se aplica mínimo por tipo
            @RequestParam(required = false) String couponCode,// sólo aplica para "aecoder"
            @RequestParam(required = false) String clerkId    // para validar single-use del cupón
    ) {
        return ResponseEntity.ok(service.getInvestmentDetail(slug, modality,planKey, quantity, couponCode, clerkId));
    }

    /** NUEVO — Enviar solicitud (sin login obligatorio) */
    @PostMapping("/{slug}/call-for-presentation/submissions")
    public ResponseEntity<CallForPresentationSubmissionDTO> submit(
            @PathVariable String slug,
            @RequestBody SubmitCallForPresentationDTO dto) {
        return ResponseEntity.ok(service.submitCallForPresentation(slug, dto));
    }

    /* ==================== NUEVO: COMPRA FRONT-ASSERTED (EVENT) ==================== */
    @PostMapping("/{slug}/access/purchase")
    public ResponseEntity<AccessEventPurchaseResponseDTO> grantEventWithFrontAssertedPurchase(
            @PathVariable String slug,
            @Valid @RequestBody AccessEventPurchaseRequestDTO request
    ) {
        var res = purchaseAccessService.processFrontAssertedEventPurchase(slug, request);
        return ResponseEntity.ok(res);
    }

    // ======== NUEVO: Participantes previos al pago (vista inversión) ========

    /** Crea/guarda un participante del carrito (previo al pago). Devuelve el groupId. */
    @PostMapping("/{slug}/investment/participants")
    public ResponseEntity<ParticipantDTO> upsertInvestmentParticipant(
            @PathVariable String slug,
            @RequestBody ParticipantCreateRequest req
    ) {
        return ResponseEntity.ok(service.upsertInvestmentParticipant(slug, req));
    }

    /** Lista los participantes PENDING de un carrito (groupId) del comprador. */
    @GetMapping("/{slug}/investment/participants")
    public ResponseEntity<ParticipantListResponse> listInvestmentParticipants(
            @PathVariable String slug,
            @RequestParam String buyerClerkId,
            @RequestParam String groupId
    ) {
        return ResponseEntity.ok(service.listInvestmentParticipants(slug, buyerClerkId, groupId));
    }

    /** Lista (agrupado por groupId) los participantes del comprador (todas las landings o filtrado por slug).
     *  status: ALL|PENDING|CONFIRMED (por defecto ALL).
     *  Útil para que el admin/usuario vea todos sus "carritos" anteriores y actuales.
     */
    @GetMapping("/admin/participants/by-buyer")
    public ResponseEntity<ParticipantGroupListResponse> listParticipantsByBuyerGrouped(
            @RequestParam String buyerClerkId,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false, defaultValue = "ALL") String status
    ) {
        return ResponseEntity.ok(service.listParticipantsByBuyerGrouped(buyerClerkId, slug, status));
    }

    /** Atajo: solo CONFIRMED (agrupado) por buyerClerkId (opcional slug). */
    @GetMapping("/admin/participants/by-buyer/confirmed")
    public ResponseEntity<ParticipantGroupListResponse> listConfirmedParticipantsByBuyerGrouped(
            @RequestParam String buyerClerkId,
            @RequestParam(required = false) String slug
    ) {
        return ResponseEntity.ok(service.listConfirmedParticipantsByBuyerGrouped(buyerClerkId, slug));
    }

    /** Elimina un participante PENDING del carrito (ownership por buyerClerkId). */
    @DeleteMapping("/{slug}/investment/participants/{participantId}")
    public ResponseEntity<ParticipantDeleteResponse> deleteInvestmentParticipant(
            @PathVariable String slug,
            @PathVariable Long participantId,
            @RequestParam String buyerClerkId
    ) {
        return ResponseEntity.ok(service.deleteInvestmentParticipant(slug, buyerClerkId, participantId));
    }

    /** (Opcional) Marcar participantes como CONFIRMED tras pago (cualquier medio). */
    @PatchMapping("/{slug}/investment/participants/mark-paid")
    public ResponseEntity<Void> markParticipantsPaid(
            @PathVariable String slug,
            @RequestParam String groupId,
            @RequestParam(required = false) String orderReference
    ) {
        service.markParticipantsConfirmedByGroup(slug, groupId, orderReference);
        return ResponseEntity.ok().build();
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

    /** NUEVO — PATCH Call for Presentation por ID */
    @PatchMapping("/admin/{id}/call-for-presentation")
    public ResponseEntity<LandingPageDTO> patchCallForPresentation(
            @PathVariable Long id, @RequestBody UpdateCallForPresentationDTO dto) {
        return ResponseEntity.ok(service.patchCallForPresentationById(id, dto));
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