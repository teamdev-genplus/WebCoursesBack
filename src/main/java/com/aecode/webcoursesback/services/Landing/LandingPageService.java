package com.aecode.webcoursesback.services.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;

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

}
