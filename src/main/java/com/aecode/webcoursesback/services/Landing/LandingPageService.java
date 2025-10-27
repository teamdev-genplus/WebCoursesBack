package com.aecode.webcoursesback.services.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.dtos.Landing.Inversion.*;
import com.aecode.webcoursesback.dtos.Landing.Solicitud.CallForPresentationSubmissionDTO;
import com.aecode.webcoursesback.dtos.Landing.Solicitud.SubmitCallForPresentationDTO;

import java.util.List;

public interface LandingPageService {
    // Público
    LandingPageDTO getBySlug(String slug);

    // Índices
    List<LandingIndexDTO> listPublicIndex(); // slug + título
    List<LandingIndexDTO> listAdminIndex();  // id + slug + título

    // Admin
    LandingPageDTO upsert(LandingPageDTO dto);

    // Admin por ID (nuevos)
    LandingPageDTO patchPrincipalById(Long id, UpdatePrincipalDTO dto);
    LandingPageDTO patchCollaboratorsById(Long id, UpdateCollaboratorsDTO dto);
    LandingPageDTO patchAboutById(Long id, UpdateAboutDTO dto);
    LandingPageDTO patchSpeakersById(Long id, UpdateSpeakersDTO dto);
    LandingPageDTO patchBenefitsById(Long id, UpdateBenefitsDTO dto);
    LandingPageDTO patchPricingById(Long id, UpdatePricingDTO dto);
    LandingPageDTO patchSocialById(Long id, UpdateSocialDTO dto);
    /** NUEVO */
    LandingPageDTO patchCallForPresentationById(Long id, UpdateCallForPresentationDTO dto);

    /**
     * Vista "Inversión": devuelve títulos de planes y, para el plan seleccionado,
     * los dos párrafos de beneficios + importes calculados.
     */
    LandingInvestmentDTO getInvestmentDetail(
            String slug,
            String modality,
            String planKey,
            Integer quantity,
            String couponCode,
            String clerkId
    );

    /** NUEVO: creación de solicitud */
    CallForPresentationSubmissionDTO submitCallForPresentation(
            String slug, SubmitCallForPresentationDTO dto);

    // === NUEVO: Participantes previos al pago (vista de inversión) ===
    ParticipantDTO upsertInvestmentParticipant(String slug, ParticipantCreateRequest req);
    ParticipantListResponse listInvestmentParticipants(String slug, String buyerClerkId, String groupId);
    ParticipantDeleteResponse deleteInvestmentParticipant(String slug, String buyerClerkId, Long participantId);

    /** (Opcional, útil cuando confirmes pago por cualquier medio) */
    void markParticipantsConfirmedByGroup(String slug, String groupId, String orderReference);
}