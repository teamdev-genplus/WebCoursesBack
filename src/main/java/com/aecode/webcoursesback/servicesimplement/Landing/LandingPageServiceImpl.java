package com.aecode.webcoursesback.servicesimplement.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.dtos.Landing.Inversion.LandingInvestmentDTO;
import com.aecode.webcoursesback.dtos.Landing.Inversion.PlanTitleDTO;
import com.aecode.webcoursesback.dtos.Landing.Inversion.SelectedPlanBenefitsDTO;
import com.aecode.webcoursesback.entities.Landing.LandingPage;
import com.aecode.webcoursesback.repositories.Landing.LandingPageRepository;
import com.aecode.webcoursesback.services.Landing.LandingPageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
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



    @Override
    @Transactional(readOnly = true)
    public LandingInvestmentDTO getInvestmentDetail(String slug, String planKey, Double taxRate, Boolean priceIncludesTax) {
        LandingPage e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));

        if (e.getPricing() == null || e.getPricing().isEmpty()) {
            throw new EntityNotFoundException("La landing no tiene planes configurados.");
        }

        // 1) Títulos para "Tipo de tarifa"
        List<PlanTitleDTO> titles = e.getPricing().stream()
                .sorted(Comparator.comparing(LandingPage.PricingPlan::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(p -> PlanTitleDTO.builder()
                        .key(p.getKey())
                        .title(p.getTitle())
                        .build())
                .toList();

        // 2) Plan seleccionado por key (o primero si no llega)
        LandingPage.PricingPlan sel = null;
        if (planKey != null && !planKey.isBlank()) {
            sel = e.getPricing().stream()
                    .filter(p -> planKey.equalsIgnoreCase(p.getKey()))
                    .findFirst()
                    .orElse(null);
        }
        if (sel == null) sel = e.getPricing().get(0);

        // === NUEVO: elegir precio efectivo (pronto pago vs regular) ===
        Double effectivePrice = null;
        if (Boolean.TRUE.equals(sel.getPromptPaymentEnabled()) && sel.getPromptPaymentPrice() != null) {
            effectivePrice = sel.getPromptPaymentPrice();
        } else {
            effectivePrice = sel.getPriceAmount();
        }
        if (effectivePrice == null) effectivePrice = 0d;

        // 3) Cálculo de importes con el precio efectivo
        double rate = Optional.ofNullable(taxRate).orElse(0.18d);
        boolean includesTax = Optional.ofNullable(priceIncludesTax).orElse(false);

        BigDecimal base = BigDecimal.valueOf(effectivePrice);
        BigDecimal r = BigDecimal.valueOf(rate);

        BigDecimal subtotal, igv, total;

        if (includesTax) {
            // effectivePrice YA incluye IGV
            BigDecimal divisor = BigDecimal.ONE.add(r);
            subtotal = base.divide(divisor, 2, RoundingMode.HALF_UP);
            igv = base.subtract(subtotal);
            total = base;
        } else {
            // effectivePrice NO incluye IGV
            subtotal = base.setScale(2, RoundingMode.HALF_UP);
            igv = subtotal.multiply(r).setScale(2, RoundingMode.HALF_UP);
            total = subtotal.add(igv).setScale(2, RoundingMode.HALF_UP);
        }

        SelectedPlanBenefitsDTO selected = SelectedPlanBenefitsDTO.builder()
                .key(sel.getKey())
                .title(sel.getTitle())
                .beforeEventText(sel.getBeforeEventText())
                .duringEventText(sel.getDuringEventText())
                .currency(sel.getCurrency())

                // precios expuestos
                .priceAmount(sel.getPriceAmount())
                .promptPaymentPrice(sel.getPromptPaymentPrice())
                .promptPaymentEnabled(Boolean.TRUE.equals(sel.getPromptPaymentEnabled()))

                // precio usado en el cálculo
                .effectiveUnitPrice(effectivePrice)

                // totales
                .subtotal(subtotal)
                .taxAmount(igv)
                .total(total)

                // parámetros de cálculo
                .taxRate(rate)
                .priceIncludesTax(includesTax)
                .build();

        return LandingInvestmentDTO.builder()
                .plans(titles)
                .selected(selected)
                .build();
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