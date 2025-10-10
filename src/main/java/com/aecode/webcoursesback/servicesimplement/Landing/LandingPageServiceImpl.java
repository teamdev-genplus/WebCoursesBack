package com.aecode.webcoursesback.servicesimplement.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.dtos.Landing.Inversion.LandingInvestmentDTO;
import com.aecode.webcoursesback.dtos.Landing.Inversion.PlanTitleDTO;
import com.aecode.webcoursesback.dtos.Landing.Inversion.SelectedPlanBenefitsDTO;
import com.aecode.webcoursesback.entities.Coupon.Coupon;
import com.aecode.webcoursesback.entities.Landing.LandingPage;
import com.aecode.webcoursesback.repositories.Coupon.CouponRedemptionRepository;
import com.aecode.webcoursesback.repositories.Coupon.CouponRepository;
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
    private final CouponRepository couponRepo;                   // NUEVO
    private final CouponRedemptionRepository redemptionRepo;     // NUEVO
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
    public LandingInvestmentDTO getInvestmentDetail(String slug, String planKey, Integer quantity,
                                                    String couponCode, String clerkId) {
        LandingPage e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));
        if (e.getPricing() == null || e.getPricing().isEmpty()) {
            throw new EntityNotFoundException("La landing no tiene planes configurados.");
        }

        // 1) Lista de títulos
        List<PlanTitleDTO> titles = e.getPricing().stream()
                .sorted(Comparator.comparing(LandingPage.PricingPlan::getTitle,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(p -> PlanTitleDTO.builder().key(p.getKey()).title(p.getTitle()).build())
                .toList();

        // 2) Plan seleccionado
        LandingPage.PricingPlan sel = null;
        if (planKey != null && !planKey.isBlank()) {
            sel = e.getPricing().stream()
                    .filter(p -> planKey.equalsIgnoreCase(p.getKey()))
                    .findFirst().orElse(null);
        }
        if (sel == null) sel = e.getPricing().get(0);
        String key = Optional.ofNullable(sel.getKey()).orElse("").toLowerCase();

        // 3) Cantidad por defecto según tipo
        int qty;
        if ("corporativo".equals(key)) {
            qty = Math.max(2, Optional.ofNullable(quantity).orElse(2));
        } else if ("general".equals(key)) {
            qty = Math.max(1, Optional.ofNullable(quantity).orElse(1));
        } else { // "aecoder" u otros
            qty = Math.max(1, Optional.ofNullable(quantity).orElse(1));
        }

        // 4) Subtotal (lo que se muestra): priceAmount * qty
        double unitPrice = Optional.ofNullable(sel.getPriceAmount()).orElse(0.0);
        BigDecimal subtotal = BigDecimal.valueOf(unitPrice).multiply(BigDecimal.valueOf(qty));

        // 5) Descuento por pronto pago (si aplica): (priceAmount - promptPaymentPrice) * qty
        BigDecimal discountPrompt = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(sel.getPromptPaymentEnabled())
                && sel.getPromptPaymentPrice() != null
                && sel.getPriceAmount() != null) {
            double diff = sel.getPriceAmount() - sel.getPromptPaymentPrice();
            if (diff > 0) {
                discountPrompt = BigDecimal.valueOf(diff).multiply(BigDecimal.valueOf(qty));
            }
        }

        // 6) Descuento por cupón (sólo si plan "aecoder" y cupón válido)
        BigDecimal discountCoupon = BigDecimal.ZERO;
        String couponApplied = null;
        if ("aecoder".equals(key) && couponCode != null && !couponCode.isBlank()) {
            var cup = couponRepo.findByCode(couponCode.trim()).orElse(null);
            if (isValidLandingCoupon(cup, slug, clerkId)) {
                discountCoupon = calcCouponDiscount(cup, subtotal); // se aplica sobre el subtotal
                if (discountCoupon.compareTo(BigDecimal.ZERO) < 0) discountCoupon = BigDecimal.ZERO;
                // no permitir que el descuento cupón exceda el saldo restante (no necesario ahora, pero seguro):
                if (discountCoupon.compareTo(subtotal) > 0) discountCoupon = subtotal;
                couponApplied = cup.getCode();
            }
        }

        BigDecimal discountTotal = discountPrompt.add(discountCoupon);
        // 7) Total: subtotal - (prompt + coupon)
        BigDecimal total = subtotal.subtract(discountTotal);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        SelectedPlanBenefitsDTO selected = SelectedPlanBenefitsDTO.builder()
                .key(sel.getKey())
                .title(sel.getTitle())
                .beforeEventText(sel.getBeforeEventText())
                .duringEventText(sel.getDuringEventText())
                .currency(sel.getCurrency())
                .priceAmount(sel.getPriceAmount())
                .promptPaymentPrice(sel.getPromptPaymentPrice())
                .promptPaymentEnabled(Boolean.TRUE.equals(sel.getPromptPaymentEnabled()))
                .quantity(qty)
                .couponCodeApplied(couponApplied)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .discountPromptPayment(discountPrompt.setScale(2, RoundingMode.HALF_UP))
                .discountCoupon(discountCoupon.setScale(2, RoundingMode.HALF_UP))
                .discountTotal(discountTotal.setScale(2, RoundingMode.HALF_UP))
                .total(total.setScale(2, RoundingMode.HALF_UP))
                .build();

        return LandingInvestmentDTO.builder()
                .plans(titles)
                .selected(selected)
                .build();
    }

    /* ===== Helpers de cupón ===== */

    private boolean isValidLandingCoupon(Coupon c, String slug, String clerkId) {
        if (c == null) return false;
        if (Boolean.FALSE.equals(c.getActive())) return false;
        var today = java.time.LocalDate.now();
        if (c.getStartDate() != null && today.isBefore(c.getStartDate())) return false;
        if (c.getEndDate() != null && today.isAfter(c.getEndDate())) return false;
        if (c.getUsageLimit() != null && c.getUsageCount() != null
                && c.getUsageCount() >= c.getUsageLimit()) return false;

        // si el cupón es específico para landing, debe coincidir el slug
        if (Boolean.TRUE.equals(c.getLandingSpecific())) {
            if (c.getLandingSlug() == null || !c.getLandingSlug().equalsIgnoreCase(slug)) return false;
        }

        // si exige un solo uso por usuario
        if (Boolean.TRUE.equals(c.getSingleUsePerUser())) {
            if (clerkId == null || clerkId.isBlank()) return false;
            boolean already = redemptionRepo.existsByCouponAndClerkId(c, clerkId);
            if (already) return false;
        }

        // Si está marcado como courseSpecific, lo ignoramos (no aplica en landing)
        // (No invalidamos por ello; simplemente no es “landing-specific”)
        return true;
    }

    private BigDecimal calcCouponDiscount(Coupon c, BigDecimal base) {
        // base = subtotal
        if (c == null || base == null) return BigDecimal.ZERO;
        if (c.getDiscountPercentage() != null) {
            double pct = c.getDiscountPercentage() / 100.0;
            if (pct < 0) pct = 0;
            if (pct > 1) pct = 1;
            return base.multiply(BigDecimal.valueOf(pct));
        }
        if (c.getDiscountAmount() != null) {
            double amt = Math.max(0, c.getDiscountAmount());
            return BigDecimal.valueOf(amt);
        }
        return BigDecimal.ZERO;
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