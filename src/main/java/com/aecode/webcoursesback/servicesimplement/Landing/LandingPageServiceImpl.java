package com.aecode.webcoursesback.servicesimplement.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.entities.Landing.LandingPage;
import com.aecode.webcoursesback.repositories.Landing.LandingPageRepository;
import com.aecode.webcoursesback.services.Landing.LandingPageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LandingPageServiceImpl implements LandingPageService {

    private final LandingPageRepository repo;
    private final ModelMapper mapper = new ModelMapper();

    private LandingPageDTO map(LandingPage e) {
        return mapper.map(e, LandingPageDTO.class);
    }
    private static String firstPrincipalTitle(LandingPage e) {
        return (e.getPrincipal() != null && !e.getPrincipal().isEmpty())
                ? Optional.ofNullable(e.getPrincipal().get(0).getTitle()).orElse("")
                : "";
    }

    private static String firstPrincipalDateLabel(LandingPage e) {
        return (e.getPrincipal() != null && !e.getPrincipal().isEmpty())
                ? Optional.ofNullable(e.getPrincipal().get(0).getDateLabel()).orElse("")
                : "";
    }
    /* ==================== PÚBLICO ==================== */

    @Override
    @Transactional(readOnly = true)
    public LandingPageDTO getBySlug(String slug) {
        LandingPage e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));
        return map(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LandingIndexDTO> listPublicIndex() {
        return repo.findAll().stream()
                .map(e -> LandingIndexDTO.builder()
                        .id(e.getId())            // opcional si no quieres exponerlo en público
                        .slug(e.getSlug())
                        .title(firstPrincipalTitle(e))
                        .dateLabel(firstPrincipalDateLabel(e))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LandingIndexDTO> listAdminIndex() {
        return repo.findAll().stream()
                .map(e -> LandingIndexDTO.builder()
                        .id(e.getId())
                        .slug(e.getSlug())
                        .title(firstPrincipalTitle(e))
                        .dateLabel(firstPrincipalDateLabel(e))
                        .build())
                .toList();
    }

    /* ==================== ADMIN ==================== */

    @Override
    public LandingPageDTO upsert(LandingPageDTO dto) {
        if (dto.getSlug() == null || dto.getSlug().isBlank())
            throw new IllegalArgumentException("slug requerido");

        LandingPage e = repo.findBySlug(dto.getSlug()).orElseGet(LandingPage::new);
        e.setSlug(dto.getSlug());

        e.setPrincipal(dto.getPrincipal());
        e.setCollaborators(dto.getCollaborators());
        e.setAbout(dto.getAbout());
        e.setSpeakers(dto.getSpeakers());
        e.setBenefits(dto.getBenefits());
        e.setPricing(dto.getPricing());
        e.setSocial(dto.getSocial());

        e = repo.save(e);
        return map(e);
    }

    // ---- PATCH por ID ----

    @Override
    public LandingPageDTO patchPrincipalById(Long id, UpdatePrincipalDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setPrincipal(dto.getPrincipal());
        return map(repo.save(e));
    }

    @Override
    public LandingPageDTO patchCollaboratorsById(Long id, UpdateCollaboratorsDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setCollaborators(dto.getCollaborators());
        return map(repo.save(e));
    }

    @Override
    public LandingPageDTO patchAboutById(Long id, UpdateAboutDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setAbout(dto.getAbout());
        return map(repo.save(e));
    }

    @Override
    public LandingPageDTO patchSpeakersById(Long id, UpdateSpeakersDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setSpeakers(dto.getSpeakers());
        return map(repo.save(e));
    }

    @Override
    public LandingPageDTO patchBenefitsById(Long id, UpdateBenefitsDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setBenefits(dto.getBenefits());
        return map(repo.save(e));
    }

    @Override
    public LandingPageDTO patchPricingById(Long id, UpdatePricingDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setPricing(dto.getPricing());
        return map(repo.save(e));
    }

    @Override
    public LandingPageDTO patchSocialById(Long id, UpdateSocialDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setSocial(dto.getSocial());
        return map(repo.save(e));
    }
}