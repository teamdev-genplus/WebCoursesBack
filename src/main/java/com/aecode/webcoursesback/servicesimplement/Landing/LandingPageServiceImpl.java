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
import com.aecode.webcoursesback.repositories.Landing.EventParticipantRepository;
import com.aecode.webcoursesback.entities.Landing.EventParticipant;
import com.aecode.webcoursesback.repositories.Landing.EventParticipantRepository;
import com.aecode.webcoursesback.dtos.Landing.Inversion.*;
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
import java.util.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;


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
    private final EventParticipantRepository eventParticipantRepo; // <-- NUEVO

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private OffsetDateTime toLima(OffsetDateTime utc) {
        return utc == null ? null : utc.atZoneSameInstant(LIMA).toOffsetDateTime();
    }

    // ===== Helpers de seguridad HTML simples (reuso de tu plantilla de emails)
    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }


    private LandingPageDTO map(LandingPage e) {
        LandingPageDTO dto = mapper.map(e, LandingPageDTO.class);
        dto.setCreatedAt(toLima(e.getCreatedAt()));
        dto.setUpdatedAt(toLima(e.getUpdatedAt()));
        return dto;
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
    public LandingInvestmentDTO getInvestmentDetail(String slug,
                                                    String modality,     // PRESENCIAL | VIRTUAL (opcional)
                                                    String planKey,
                                                    Integer quantity,
                                                    String couponCode,
                                                    String clerkId) {
        LandingPage e = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));
        if (e.getPricing() == null || e.getPricing().isEmpty()) {
            throw new EntityNotFoundException("La landing no tiene planes configurados.");
        }

        // 0) Modalidades disponibles
        List<String> availableModalities = e.getPricing().stream()
                .map(LandingPage.PricingPlan::getModality)
                .filter(Objects::nonNull)
                .map(this::normalizeModality)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        List<ModalityTitleDTO> modalityTitles = availableModalities.stream()
                .map(m -> ModalityTitleDTO.builder().key(m).title(m).build())
                .toList();

        // 1) Determinar modalidad activa
        String requestedMod = normalizeModality(modality);
        String activeModality;
        if (requestedMod != null && availableModalities.contains(requestedMod)) {
            activeModality = requestedMod;
        } else {
            // fallback: primera modalidad presente; si no hay, sin modalidad (planes legacy)
            activeModality = availableModalities.isEmpty() ? null : availableModalities.get(0);
        }

        // 2) Filtrar planes por modalidad activa
        List<LandingPage.PricingPlan> pool = e.getPricing().stream()
                .filter(p -> Objects.equals(normalizeModality(p.getModality()), activeModality))
                .toList();

        // Compatibilidad legacy: si no hay planes etiquetados con modalidad, usa todos
        if (pool.isEmpty()) {
            pool = e.getPricing();
        }
        if (pool.isEmpty()) {
            throw new EntityNotFoundException("No hay planes disponibles para la modalidad seleccionada.");
        }

        // 3) Títulos de planes (solo de la modalidad activa)
        List<PlanTitleDTO> titles = pool.stream()
                .sorted(Comparator.comparing(LandingPage.PricingPlan::getTitle,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(p -> PlanTitleDTO.builder().key(p.getKey()).title(p.getTitle()).build())
                .toList();

        // 4) Seleccionar plan dentro del pool filtrado
        LandingPage.PricingPlan sel = null;
        if (planKey != null && !planKey.isBlank()) {
            sel = pool.stream()
                    .filter(p -> planKey.equalsIgnoreCase(nvl(p.getKey(), "")))
                    .findFirst().orElse(null);
        }
        if (sel == null) sel = pool.get(0);

        final String key = Optional.ofNullable(sel.getKey()).orElse("").toLowerCase();

        // 5) Determinar cantidad (qty) y política de formularios
        final int minQty;
        final String participantMode;
        if ("corporativo".equals(key)) {
            minQty = 2;
            participantMode = "EXCLUDE_BUYER";
        } else {
            minQty = 1;
            participantMode = "INCLUDE_BUYER";
        }
        int qty = Optional.ofNullable(quantity).orElse(minQty);
        if (qty < minQty) qty = minQty;

        final int requiredForms = "corporativo".equals(key) ? qty : Math.max(0, qty - 1);

        // 6) Cálculos monetarios
        double unitPrice = Optional.ofNullable(sel.getPriceAmount()).orElse(0.0);
        BigDecimal subtotal = BigDecimal.valueOf(unitPrice).multiply(BigDecimal.valueOf(qty));

        BigDecimal discountPrompt = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(sel.getPromptPaymentEnabled())
                && sel.getPromptPaymentPrice() != null
                && sel.getPriceAmount() != null) {
            double diff = sel.getPriceAmount() - sel.getPromptPaymentPrice();
            if (diff > 0) {
                discountPrompt = BigDecimal.valueOf(diff).multiply(BigDecimal.valueOf(qty));
            }
        }

        BigDecimal discountCoupon = BigDecimal.ZERO;
        String couponApplied = null;
        // Solo permites cupón para aecoder (manteniendo tu regla actual)
        if ("aecoder".equals(key) && couponCode != null && !couponCode.isBlank()) {
            var cup = couponRepo.findByCode(couponCode.trim()).orElse(null);
            if (isValidLandingCouponPreview(cup, slug)) {
                discountCoupon = calcCouponDiscount(cup, subtotal);
                if (discountCoupon.compareTo(BigDecimal.ZERO) < 0) discountCoupon = BigDecimal.ZERO;
                if (discountCoupon.compareTo(subtotal) > 0) discountCoupon = subtotal;
                couponApplied = cup.getCode();
            }
        }

        BigDecimal discountTotal = discountPrompt.add(discountCoupon);
        BigDecimal total = subtotal.subtract(discountTotal);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        SelectedPlanBenefitsDTO selected = SelectedPlanBenefitsDTO.builder()
                .key(sel.getKey())
                .title(sel.getTitle())
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
                .participantMode(participantMode)
                .minQuantity(minQty)
                .requiredParticipantForms(requiredForms)
                .build();

        return LandingInvestmentDTO.builder()
                .modalities(modalityTitles)      // <--- NUEVO
                .activeModality(activeModality)  // <--- NUEVO
                .plans(titles)
                .selected(selected)
                .build();
    }


    // ===== Helpers nuevos/ajustados =====
    private String normalizeModality(String s) {
        if (s == null) return null;
        String up = s.trim().toUpperCase();
        if (up.isBlank()) return null;
        // acepta variantes comunes
        if (up.startsWith("PRES")) return "PRESENCIAL";
        if (up.startsWith("VIRT")) return "VIRTUAL";
        // si mandan otra cosa, igual retorna en mayúsculas para posible match estricto
        return up;
    }
    private String nvl(String s, String def) { return (s == null || s.isBlank()) ? def : s; }


    /* ===== Helpers de cupón ===== */

    /** Validación para PREVISUALIZAR descuentos en /investment:
     *  - Aplica reglas de vigencia, uso y landingSlug
     *  - NO exige clerkId ni verifica single-use-per-user
     */
    private boolean isValidLandingCouponPreview(Coupon c, String slug) {
        if (c == null) return false;
        if (Boolean.FALSE.equals(c.getActive())) return false;

        var today = java.time.LocalDate.now();
        if (c.getStartDate() != null && today.isBefore(c.getStartDate())) return false;
        if (c.getEndDate() != null && today.isAfter(c.getEndDate())) return false;

        if (c.getUsageLimit() != null && c.getUsageCount() != null
                && c.getUsageCount() >= c.getUsageLimit()) return false;

        // Si es específico de landing, debe coincidir
        if (Boolean.TRUE.equals(c.getLandingSpecific())) {
            return c.getLandingSlug() != null && c.getLandingSlug().equalsIgnoreCase(slug);
        }

        // Si no es landingSpecific, lo consideramos cupón "global" válido también para landing
        return true;
    }


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
        if (c == null || base == null) return BigDecimal.ZERO;

        // 1) Monto fijo tiene prioridad SI es > 0
        Double amount = c.getDiscountAmount();
        if (amount != null && amount > 0.0) {
            double amt = Math.max(0.0, amount);
            return BigDecimal.valueOf(amt);
        }

        // 2) Si no hay monto fijo, usar % sólo si > 0
        Double percentage = c.getDiscountPercentage();
        if (percentage != null && percentage > 0.0) {
            double pct = Math.min(1.0, Math.max(0.0, percentage / 100.0));
            return base.multiply(BigDecimal.valueOf(pct));
        }

        // 3) Ningún descuento válido
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


    // =================== PARTICIPANTES (Nueva Sección) ===================

    @Override
    public ParticipantDTO upsertInvestmentParticipant(String slug, ParticipantCreateRequest req) {
        // 1) Validaciones mínimas
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException("slug requerido");
        if (req == null) throw new IllegalArgumentException("payload requerido");

        // landing existente
        var landing = repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));

        String modality = normalizeModality(req.getModality());
        if (modality == null) throw new IllegalArgumentException("modality requerida (PRESENCIAL|VIRTUAL)");

        String planKey = nvl(req.getPlanKey(), "").toLowerCase();
        if (planKey.isBlank()) throw new IllegalArgumentException("planKey requerido");

        String buyerClerkId = nvl(req.getBuyerClerkId(), "");
        if (buyerClerkId.isBlank()) throw new IllegalArgumentException("buyerClerkId requerido");

        Integer quantity = req.getQuantity();
        if (quantity == null || quantity < 1) throw new IllegalArgumentException("quantity inválido");

        Integer pIndex = req.getParticipantIndex();
        if (pIndex == null || pIndex < 1 || pIndex > quantity)
            throw new IllegalArgumentException("participantIndex inválido (1..quantity)");

        // 2) Reglas por plan
        // general/aecoder: mínimo 1. El #1 es el comprador -> no registrar formulario para #1
        // corporativo: mínimo 2. Se registran todos (#1..quantity) mediante formularios.
        if ("general".equals(planKey) || "aecoder".equals(planKey)) {
            if (quantity < 1) throw new IllegalArgumentException("quantity mínimo para plan " + planKey + " es 1");
            if (pIndex == 1) {
                throw new IllegalArgumentException("El participante #1 es el comprador; no requiere formulario.");
            }
        } else if ("corporativo".equals(planKey)) {
            if (quantity < 2) throw new IllegalArgumentException("quantity mínimo para plan corporativo es 2");
        }

        // 3) Validar que planKey exista para la modalidad
        boolean planExists = landing.getPricing() != null && landing.getPricing().stream()
                .filter(p -> Objects.equals(normalizeModality(p.getModality()), modality))
                .anyMatch(p -> planKey.equalsIgnoreCase(nvl(p.getKey(), "")));
        if (!planExists) throw new IllegalArgumentException("El plan no existe para la modalidad indicada.");

        // 4) Validar campos de participante
        if (isBlank(req.getFirstName())) throw new IllegalArgumentException("firstName requerido");
        if (isBlank(req.getLastName())) throw new IllegalArgumentException("lastName requerido");
        if (isBlank(req.getEmail())) throw new IllegalArgumentException("email requerido");
        if (isBlank(req.getPhone())) throw new IllegalArgumentException("phone requerido");
        if (isBlank(req.getDocumentType())) throw new IllegalArgumentException("documentType requerido");
        if (isBlank(req.getDocumentNumber())) throw new IllegalArgumentException("documentNumber requerido");

        // 5) groupId (si no llega, crear uno y retornarlo)
        String groupId = req.getGroupId();
        if (groupId == null || groupId.isBlank()) {
            groupId = UUID.randomUUID().toString();
        }

        // Evitar duplicados de índice por (slug, groupId, participantIndex)
        if (eventParticipantRepo.existsByLandingSlugAndGroupIdAndParticipantIndex(slug, groupId, pIndex)) {
            throw new IllegalStateException("Ya existe un participante con participantIndex=" + pIndex + " en este grupo.");
        }

        // 6) Persistir
        var ep = EventParticipant.builder()
                .landingSlug(slug)
                .modality(modality)
                .planKey(planKey)
                .buyerClerkId(buyerClerkId)
                .groupId(groupId)
                .participantIndex(pIndex)
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .email(req.getEmail().trim())
                .phone(req.getPhone().trim())
                .documentType(req.getDocumentType().trim())
                .documentNumber(req.getDocumentNumber().trim())
                .company(nvl(req.getCompany(), null))
                .status(EventParticipant.Status.PENDING)
                .build();

        ep = eventParticipantRepo.save(ep);
        return toDTO(ep);
    }

    @Override
    @Transactional(readOnly = true)
    public ParticipantListResponse listInvestmentParticipants(String slug, String buyerClerkId, String groupId) {
        if (isBlank(slug)) throw new IllegalArgumentException("slug requerido");
        if (isBlank(buyerClerkId)) throw new IllegalArgumentException("buyerClerkId requerido");
        if (isBlank(groupId)) throw new IllegalArgumentException("groupId requerido");

        var list = eventParticipantRepo.findByLandingSlugAndBuyerClerkIdAndGroupId(slug, buyerClerkId, groupId)
                .stream().map(this::toDTO).toList();

        return ParticipantListResponse.builder()
                .groupId(groupId)
                .participants(list)
                .build();
    }

    @Override
    public ParticipantDeleteResponse deleteInvestmentParticipant(String slug, String buyerClerkId, Long participantId) {
        if (participantId == null) throw new IllegalArgumentException("participantId requerido");
        var ep = eventParticipantRepo.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participante no encontrado"));

        if (!slug.equals(ep.getLandingSlug()))
            throw new IllegalArgumentException("slug no coincide con el participante");

        if (!buyerClerkId.equals(ep.getBuyerClerkId()))
            throw new IllegalArgumentException("No autorizado para eliminar este participante");

        if (ep.getStatus() != EventParticipant.Status.PENDING)
            throw new IllegalStateException("Solo se pueden eliminar participantes en estado PENDING");

        eventParticipantRepo.delete(ep);
        return ParticipantDeleteResponse.builder().deleted(true).build();
    }

    @Override
    public void markParticipantsConfirmedByGroup(String slug, String groupId, String orderReference) {
        if (isBlank(slug) || isBlank(groupId))
            throw new IllegalArgumentException("slug y groupId requeridos");
        var list = eventParticipantRepo.findByLandingSlugAndGroupId(slug, groupId);
        for (var ep : list) {
            ep.setStatus(EventParticipant.Status.CONFIRMED);
            ep.setOrderReference(orderReference);
        }
        eventParticipantRepo.saveAll(list);
    }

    // ---- helpers de DTO ----
    private ParticipantDTO toDTO(EventParticipant ep) {
        return ParticipantDTO.builder()
                .id(ep.getId())
                .landingSlug(ep.getLandingSlug())
                .modality(ep.getModality())
                .planKey(ep.getPlanKey())
                .buyerClerkId(ep.getBuyerClerkId())
                .groupId(ep.getGroupId())
                .participantIndex(ep.getParticipantIndex())
                .firstName(ep.getFirstName())
                .lastName(ep.getLastName())
                .email(ep.getEmail())
                .phone(ep.getPhone())
                .documentType(ep.getDocumentType())
                .documentNumber(ep.getDocumentNumber())
                .company(ep.getCompany())
                .status(ep.getStatus().name())
                .orderReference(ep.getOrderReference())
                .createdAt(toLima(ep.getCreatedAt()))
                .updatedAt(toLima(ep.getUpdatedAt()))
                .build();
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }


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
                .createdAt(s.getCreatedAt() != null ? toLima(s.getCreatedAt()).toString() : null)
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