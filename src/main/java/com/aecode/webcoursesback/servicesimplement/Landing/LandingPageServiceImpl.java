package com.aecode.webcoursesback.servicesimplement.Landing;
import com.aecode.webcoursesback.dtos.Landing.*;
import com.aecode.webcoursesback.dtos.Landing.Inversion.LandingInvestmentDTO;
import com.aecode.webcoursesback.dtos.Landing.Inversion.PlanTitleDTO;
import com.aecode.webcoursesback.dtos.Landing.Inversion.SelectedPlanBenefitsDTO;
import com.aecode.webcoursesback.dtos.Landing.Solicitud.CallForPresentationSubmissionDTO;
import com.aecode.webcoursesback.dtos.Landing.Solicitud.SubmitCallForPresentationDTO;
import com.aecode.webcoursesback.entities.Coupon.Coupon;
import com.aecode.webcoursesback.entities.Landing.CallForPresentationSubmission;
import com.aecode.webcoursesback.entities.Landing.LandingPage;
import com.aecode.webcoursesback.repositories.Coupon.CouponRedemptionRepository;
import com.aecode.webcoursesback.repositories.Coupon.CouponRepository;
import com.aecode.webcoursesback.repositories.Landing.CallForPresentationSubmissionRepository;
import com.aecode.webcoursesback.repositories.Landing.LandingPageRepository;
import com.aecode.webcoursesback.services.EmailSenderService;
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
    private final CouponRepository couponRepo;
    private final CouponRedemptionRepository redemptionRepo;
    private final ModelMapper mapper = new ModelMapper();


    private final CallForPresentationSubmissionRepository submissionRepo; // NUEVO
    private final EmailSenderService emailSenderService;                 // NUEVO
    // ===== Helpers de seguridad HTML simples (reuso de tu plantilla de emails)
    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }


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
    /** ADMIN — PATCH Call for Presentation */
    @Override
    public LandingPageDTO patchCallForPresentationById(Long id, UpdateCallForPresentationDTO dto) {
        LandingPage e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada (id=" + id + ")"));
        e.setCallForPresentation(dto.getCallForPresentation());
        return mapper.map(repo.save(e), LandingPageDTO.class);
    }

    /** PUBLIC — create submission */
    @Override
    public CallForPresentationSubmissionDTO submitCallForPresentation(String slug, SubmitCallForPresentationDTO dto) {
        // 1) Valida landing
        LandingPage e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));

        // 2) Validaciones mínimas
        if (dto.getFullName() == null || dto.getFullName().isBlank())
            throw new IllegalArgumentException("fullName es requerido");
        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new IllegalArgumentException("email es requerido");
        if (dto.getCountryCode() == null || dto.getCountryCode().isBlank())
            throw new IllegalArgumentException("countryCode es requerido");
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank())
            throw new IllegalArgumentException("phoneNumber es requerido");
        if (dto.getIdeaText() == null || dto.getIdeaText().isBlank())
            throw new IllegalArgumentException("ideaText es requerido");

        // 3) Persiste
        CallForPresentationSubmission s = CallForPresentationSubmission.builder()
                .landingSlug(slug)
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim())
                .countryCode(dto.getCountryCode().trim())
                .phoneNumber(dto.getPhoneNumber().trim())
                .ideaText(dto.getIdeaText().trim())
                .clerkId(dto.getClerkId())
                .status(CallForPresentationSubmission.Status.PENDING)
                .build();

        s = submissionRepo.save(s);

        // 4) Emails (HTML al usuario, simple a empresa)
        sendUserConfirmEmail(s, e);
        sendCompanyNotifyEmail(s, e);

        // 5) Respuesta
        return CallForPresentationSubmissionDTO.builder()
                .id(s.getId())
                .landingSlug(s.getLandingSlug())
                .fullName(s.getFullName())
                .email(s.getEmail())
                .countryCode(s.getCountryCode())
                .phoneNumber(s.getPhoneNumber())
                .ideaText(s.getIdeaText())
                .status(s.getStatus().name())
                .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                .build();
    }

    /* ================= Emails ================= */

    private void sendUserConfirmEmail(CallForPresentationSubmission s, LandingPage landing) {
        String title = (landing.getCallForPresentation() != null &&
                landing.getCallForPresentation().getTitle() != null)
                ? landing.getCallForPresentation().getTitle()
                : "Call for Presentation";

        String subject = "¡Solicitud recibida! " + title + " — AECODE";
        String html = buildUserHtml(s, title);
        try {
            emailSenderService.sendHtmlEmail(s.getEmail(), subject, html);
        } catch (Exception ex) {
            System.err.println("Fallo enviando email al usuario: " + ex.getMessage());
        }
    }

    private void sendCompanyNotifyEmail(CallForPresentationSubmission s, LandingPage landing) {
        String companyEmail = "contacto@aecode.ai"; // ajusta si lo necesitas
        String subject = "Nueva solicitud — " + s.getLandingSlug();

        String body = String.format(
                java.util.Locale.US,
                "Nueva solicitud de 'Call for Presentation'\n\n" +
                        "Landing: %s\n" +
                        "Nombre: %s\n" +
                        "Email: %s\n" +
                        "Teléfono: %s %s\n\n" +
                        "Idea:\n%s\n\n" +
                        "Estado: %s\n" +
                        "Creado: %s\n",
                s.getLandingSlug(),
                s.getFullName(),
                s.getEmail(),
                s.getCountryCode(), s.getPhoneNumber(),
                s.getIdeaText(),
                s.getStatus().name(),
                s.getCreatedAt() != null ? s.getCreatedAt().toString() : "(ahora)"
        );
        try {
            emailSenderService.sendEmail(companyEmail, subject, body);
        } catch (Exception ex) {
            System.err.println("Fallo enviando email a la empresa: " + ex.getMessage());
        }
    }

    private String buildUserHtml(CallForPresentationSubmission s, String sectionTitle) {
        String fullPhone = safe(s.getCountryCode()) + " " + safe(s.getPhoneNumber());
        String niceDate = java.time.OffsetDateTime.now()
                .toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofPattern(
                        "dd 'de' MMMM 'del' yyyy", new java.util.Locale("es","ES")));

        String template = """
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Solicitud recibida</title>
</head>
<body style="background:#FAFAFA;margin:0;padding:0;font-family:Verdana, Geneva, sans-serif;">
  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#FAFAFA;">
    <tr>
      <td align="center">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background:#FFFFFF;box-shadow:0 2px 10px rgba(0,0,0,.06);">
          <tr>
            <td style="padding:0;" align="center">
              <img src="https://euqtuhd.stripocdn.email/content/guids/CABINET_d1422edc264bd643c8af51440e8995acef2448ffb48805c1983bece0ea0a568e/images/channels4_banner.jpg" width="600" style="display:block;border:0;max-width:100%%;height:auto;" alt="AECODE">
            </td>
          </tr>

          <tr>
            <td style="padding:22px 24px 8px 24px;" align="center">
              <h1 style="Margin:0;color:#333;font-size:28px;line-height:1.2;">¡Tu solicitud fue recibida!</h1>
              <p style="Margin:10px 0 0 0;color:#333;font-size:14px;line-height:1.6;">
                Gracias, <strong>%s</strong>. Hemos recibido tu solicitud a
                <strong>%s</strong>.
              </p>
              <p style="Margin:6px 0 14px 0;color:#333;font-size:13px;">Fecha: %s</p>
            </td>
          </tr>

          <tr>
            <td style="padding:0 24px 0 24px;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-top:2px solid #efefef;border-bottom:2px solid #efefef;">
                <tr>
                  <td style="padding:12px 0;color:#333;font-size:14px;">
                    <strong>Correo:</strong> %s
                  </td>
                  <td style="padding:12px 0;color:#333;font-size:14px;" align="right">
                    <strong>Teléfono:</strong> %s
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <tr>
            <td style="padding:16px 24px 6px 24px;">
              <p style="Margin:0 0 8px 0;color:#333;font-size:14px;"><strong>Resumen de tu idea</strong></p>
              <div style="padding:14px 16px;background:#F7F7FF;border-left:4px solid #5C68E2;border-radius:6px;color:#333;font-size:14px;line-height:1.6;">
                %s
              </div>
            </td>
          </tr>

          <tr>
            <td style="padding:18px 24px 24px 24px;color:#333;font-size:13px;line-height:1.6;" align="center">
              Nuestro equipo revisará tu propuesta y nos pondremos en contacto a tu correo.<br>
              Si tienes preguntas, escríbenos a
              <a href="mailto:contacto@aecode.ai" style="color:#5C68E2;text-decoration:underline;">contacto@aecode.ai</a>.
            </td>
          </tr>

          <tr>
            <td align="center" style="padding:0 24px 24px 24px;">
              <div style="display:inline-block;background:#1f1748;color:#FFFFFF;padding:10px 20px;border-radius:6px;font-weight:bold;font-size:14px;">
                AECODE Training
              </div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
""";

        // Usa String.format (o template.formatted(...)) — recuerda que %% arriba es para los % literales.
        return String.format(
                template,
                safe(s.getFullName()),
                safe(sectionTitle),
                safe(niceDate),
                safe(s.getEmail()),
                fullPhone,
                safe(s.getIdeaText())
        );
    }
}