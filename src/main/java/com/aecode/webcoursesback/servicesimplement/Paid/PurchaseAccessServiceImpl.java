package com.aecode.webcoursesback.servicesimplement.Paid;
import com.aecode.webcoursesback.dtos.Paid.Event.AccessEventPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.Event.AccessEventPurchaseResponseDTO;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseResponseDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Landing.LandingPage;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Paid.EmailReceiptRenderer;
import com.aecode.webcoursesback.entities.Paid.EventEmailReceiptRenderer;
import com.aecode.webcoursesback.entities.Paid.PaymentReceipt;
import com.aecode.webcoursesback.entities.Paid.PaymentReceiptItem;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.repositories.Landing.LandingPageRepository;
import com.aecode.webcoursesback.repositories.Paid.PaymentReceiptItemRepository;
import com.aecode.webcoursesback.repositories.Paid.PaymentReceiptRepository;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.IUserAccessService;
import com.aecode.webcoursesback.services.Landing.LandingPageService;
import com.aecode.webcoursesback.services.Paid.PurchaseAccessService;
import com.aecode.webcoursesback.services.Paid.UnifiedPaidOrderService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseAccessServiceImpl implements PurchaseAccessService {
    private final IUserProfileRepository userProfileRepo;
    private final IModuleRepo moduleRepo;
    private final PaymentReceiptRepository receiptRepo;
    private final PaymentReceiptItemRepository receiptItemRepo;
    private final IUserAccessService userAccessService;
    private final EmailSenderService emailSenderService;

    private final UnifiedPaidOrderService unifiedPaidOrderService;
    // NUEVO
    private final IUserModuleRepo userModuleRepo;

    // ====== NUEVO: LANDING ======
    private final LandingPageRepository landingRepo;
    private final LandingPageService landingService;

    // ===== helpers para precios =====
    private static BigDecimal unitPrice(Module m) {
        boolean onSale = Boolean.TRUE.equals(m.getIsOnSale());
        Double prompt = m.getPromptPaymentPrice();
        Double regular = m.getPriceRegular();
        double chosen = onSale && prompt != null && prompt > 0 ? prompt
                : regular != null ? regular : 0.0;
        return BigDecimal.valueOf(chosen).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal subtotalFromModules(List<Module> modules) {
        return modules.stream()
                .map(PurchaseAccessServiceImpl::unitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    @Override
    public AccessPurchaseResponseDTO processFrontAssertedPurchase(AccessPurchaseRequestDTO req) {
        // 1) Usuario
        UserProfile user = userProfileRepo.findByClerkId(req.getClerkId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ClerkId no encontrado: " + req.getClerkId()));

        // 2) Pedidos originales (distinct para evitar repetidos en input)
        List<Long> requestedIds = req.getModuleIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (requestedIds.isEmpty()) {
            throw new EntityNotFoundException("No se recibieron módulos a comprar.");
        }

        // 3) Módulos ya poseídos por el usuario
        Set<Long> ownedIds = userModuleRepo.findByUserProfile_ClerkId(req.getClerkId()).stream()
                .filter(a -> a.getModule() != null)
                .map(a -> a.getModule().getModuleId())
                .collect(Collectors.toSet());

        // 4) Particionar: a conceder vs a omitir
        List<Long> toGrantIds = requestedIds.stream().filter(id -> !ownedIds.contains(id)).toList();
        List<Long> skippedIds = requestedIds.stream().filter(ownedIds::contains).toList();

        // 5) Validar existencia solo de los a conceder
        List<Module> modulesToGrant = moduleRepo.findAllById(toGrantIds);
        if (modulesToGrant.size() != toGrantIds.size()) {
            throw new EntityNotFoundException("Uno o más módulos no existen (entre los no poseídos).");
        }

        // ===== precios (sobre los que se conceden, para consistencia) =====
        BigDecimal effectiveSubtotal = Optional.ofNullable(req.getSubtotal())
                .orElseGet(() -> subtotalFromModules(modulesToGrant));

        BigDecimal effectiveCommission = Optional.ofNullable(req.getCommission())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal effectiveDiscount = Optional.ofNullable(req.getDiscount())
                .orElseGet(() -> {
                    BigDecimal d = effectiveSubtotal.add(effectiveCommission).subtract(req.getTotal());
                    if (d.compareTo(BigDecimal.ZERO) < 0) d = BigDecimal.ZERO;
                    return d;
                })
                .setScale(2, RoundingMode.HALF_UP);

        // 6) Buscar o crear recibo (idempotencia por purchaseNumber)
        PaymentReceipt receipt = receiptRepo.findByPurchaseNumber(req.getPurchaseNumber())
                .orElseGet(() -> {
                    PaymentReceipt r = PaymentReceipt.builder()
                            .purchaseNumber(req.getPurchaseNumber())
                            .clerkId(user.getClerkId())
                            .userFullName(user.getFullname())
                            .userEmail(user.getEmail())
                            .purchaseAt(Optional.ofNullable(req.getPurchaseAt()).orElse(OffsetDateTime.now()))
                            .purchaseDateLabel(req.getPurchaseDateLabel())
                            .method(PaymentReceipt.PaymentMethod.valueOf(req.getMethod().name()))
                            .currency(PaymentReceipt.CurrencyCode.valueOf(req.getCurrency().name()))
                            .subtotal(effectiveSubtotal.setScale(2, RoundingMode.HALF_UP))
                            .discount(effectiveDiscount)
                            .commission(effectiveCommission)
                            .total(req.getTotal().setScale(2, RoundingMode.HALF_UP))
                            .moduleIdsCsv(toGrantIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                            .entitlementsGranted(false)
                            .build();
                    return receiptRepo.save(r);
                });

        // 7) Items del recibo (solo de los concedibles)
        if (receiptItemRepo.findByReceipt_Id(receipt.getId()).isEmpty()) {
            List<PaymentReceiptItem> items = new ArrayList<>();
            for (Module m : modulesToGrant) {
                items.add(PaymentReceiptItem.builder()
                        .receipt(receipt)
                        .moduleId(m.getModuleId())
                        .courseTitle(m.getCourse() != null ? m.getCourse().getTitle() : null)
                        .moduleTitle(m.getProgramTitle())
                        .build());
            }
            receiptItemRepo.saveAll(items);
        }

        // 8) Conceder accesos SOLO a los no poseídos (idempotente del recibo)
        List<UserModuleDTO> granted;
        if (!toGrantIds.isEmpty()) {
            int updated = receiptRepo.markGrantedIfNotGranted(receipt.getId());
            if (updated == 1) {
                // Primera vez
                granted = userAccessService.grantMultipleModuleAccess(req.getClerkId(), toGrantIds);

                // Enviar emails
                try {
                    String html = EmailReceiptRenderer.renderLegacyExact(user, modulesToGrant, receipt);
                    emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", html);
                    String plain = EmailReceiptRenderer.renderCompanyPlain(user, modulesToGrant, receipt);
                    emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra (front-asserted)", plain);
                } catch (MessagingException ignore) {}

                // Registrar en unificada (evita duplicados y solo módulos concedidos)
                try {
                    unifiedPaidOrderService.logPaidOrder(
                            user.getEmail(),
                            Optional.ofNullable(user.getFullname()).orElse(""),
                            receipt.getPurchaseAt(),
                            toGrantIds
                    );
                } catch (Exception ignore) {}
            } else {
                // Ya marcado: devolver los concedidos que intersectan con toGrantIds
                granted = userAccessService.getUserModulesByClerkId(req.getClerkId()).stream()
                        .filter(dto -> toGrantIds.contains(dto.getModuleId()))
                        .toList();
            }
        } else {
            // No hay nada que conceder (todo era duplicado)
            granted = List.of();
        }

        return AccessPurchaseResponseDTO.builder()
                .purchaseNumber(receipt.getPurchaseNumber())
                .clerkId(user.getClerkId())
                .email(user.getEmail())
                .fullName(user.getFullname())
                .purchaseAt(receipt.getPurchaseAt())
                .grantedModules(granted)
                .skippedModuleIds(skippedIds) // 👈 NUEVO: informar omitidos
                .build();
    }

    // ========= NUEVO: FRONT-ASSERTED EVENT =========
    @Transactional
    @Override
    public AccessEventPurchaseResponseDTO processFrontAssertedEventPurchase(String slug, AccessEventPurchaseRequestDTO req) {

        // 1) Usuario
        UserProfile user = userProfileRepo.findByClerkId(req.getClerkId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario con ClerkId no encontrado: " + req.getClerkId()));

        // 2) Landing + plan
        LandingPage lp = landingRepo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Landing no encontrada: " + slug));

        String eventTitle = slug;
        if (lp.getPrincipal() != null && !lp.getPrincipal().isEmpty()) {
            String t = lp.getPrincipal().get(0).getTitle();
            if (t != null && !t.isBlank()) eventTitle = t;
        }

        String planTitle = req.getPlanKey();
        if (lp.getPricing() != null) {
            planTitle = lp.getPricing().stream()
                    .filter(p -> req.getPlanKey().equalsIgnoreCase(p.getKey()))
                    .map(LandingPage.PricingPlan::getTitle)
                    .findFirst()
                    .orElse(req.getPlanKey());
        }

        int quantity = Optional.ofNullable(req.getQuantity()).orElse(1);
        String currency = req.getCurrency().name();

        // 3) Totales efectivos (si front no manda alguno, completar para email/registro)
        BigDecimal subtotal   = optionalScale(req.getSubtotal());
        BigDecimal commission = optionalScale(req.getCommission());
        BigDecimal total      = requiredScale(req.getTotal());
        BigDecimal discount   = optionalScale(req.getDiscount());

        // Subtotal fallback si no llegó: usar Investment para consistencia del correo
        if (subtotal == null) {
            var inv = landingService.getInvestmentDetail(
                    slug,
                    req.getPlanKey(),
                    quantity,
                    req.getCouponCode(),
                    req.getClerkId() // por si el cupón es single-use-per-user
            );
            var sel = inv.getSelected();
            if (sel != null && sel.getSubtotal() != null) {
                subtotal = sel.getSubtotal().setScale(2, RoundingMode.HALF_UP);
            } else {
                // último fallback: total + descuento - comisión
                BigDecimal d = discount != null ? discount : BigDecimal.ZERO;
                BigDecimal c = commission != null ? commission : BigDecimal.ZERO;
                subtotal = total.add(d).subtract(c).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            }
        }

        if (commission == null) commission = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (discount == null) {
            BigDecimal d = subtotal.add(commission).subtract(total);
            if (d.compareTo(BigDecimal.ZERO) < 0) d = BigDecimal.ZERO;
            discount = d.setScale(2, RoundingMode.HALF_UP);
        }

        // Precio unitario mostrado (para la fila del correo)
        BigDecimal unitPriceShown = subtotal.divide(BigDecimal.valueOf(Math.max(1, quantity)), 2, RoundingMode.HALF_UP);

        // ===== CONGELAMOS VARIABLES EN FINALES PARA USARLAS EN EL LAMBDA =====
        final BigDecimal subtotalF   = subtotal;
        final BigDecimal discountF   = discount;
        final BigDecimal commissionF = commission;
        final BigDecimal totalF      = total;

        final Integer     quantityF      = quantity;
        final String      planKeyF       = req.getPlanKey();
        final String      couponF        = req.getCouponCode();
        final OffsetDateTime purchaseAtF = Optional.ofNullable(req.getPurchaseAt()).orElse(OffsetDateTime.now());
        final PaymentReceipt.PaymentMethod methodF   =
                PaymentReceipt.PaymentMethod.valueOf(req.getMethod().name());
        final PaymentReceipt.CurrencyCode currencyF  =
                PaymentReceipt.CurrencyCode.valueOf(req.getCurrency().name());

        // 4) Buscar o crear recibo (idempotencia por purchaseNumber)
        PaymentReceipt receipt = receiptRepo.findByPurchaseNumber(req.getPurchaseNumber())
                .orElseGet(() -> {
                    PaymentReceipt r = PaymentReceipt.builder()
                            .purchaseNumber(req.getPurchaseNumber())
                            .clerkId(user.getClerkId())
                            .userFullName(user.getFullname())
                            .userEmail(user.getEmail())
                            .purchaseAt(purchaseAtF)
                            .purchaseDateLabel(req.getPurchaseDateLabel())
                            .method(methodF)
                            .currency(currencyF)
                            .subtotal(subtotalF)
                            .discount(discountF)
                            .commission(commissionF)
                            .total(totalF)
                            .domain(PaymentReceipt.PaymentDomain.EVENT)
                            .eventSlug(slug)
                            .eventPlanKey(planKeyF)
                            .eventQuantity(quantityF)
                            .eventCouponCode(couponF)
                            .entitlementsGranted(false)
                            .build();
                    return receiptRepo.save(r);
                });

        // 5) Enviar emails SOLO la primera vez (idempotencia)
        boolean emailed = false;
        int updated = receiptRepo.markGrantedIfNotGranted(receipt.getId());
        if (updated == 1) {
            emailed = true;
            // HTML usuario
            try {
                String html = EventEmailReceiptRenderer.renderUserHtml(
                        user, eventTitle, planTitle, quantity, currency,
                        unitPriceShown, subtotal, discount, commission, total,
                        receipt.getPurchaseNumber(), receipt.getPurchaseAt(),
                        methodF
                );
                emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", html);

                // Texto empresa
                String plain = EventEmailReceiptRenderer.renderCompanyPlain(
                        user, eventTitle, planTitle, quantity, currency,
                        unitPriceShown, subtotal, discount, commission, total,
                        receipt.getPurchaseAt(), receipt.getPurchaseNumber(),
                        methodF
                );
                String subj = String.format(Locale.US, "Nueva compra de evento (%s)", methodF.name());
                emailSenderService.sendEmail("contacto@aecode.ai", subj, plain);
            } catch (MessagingException ignore) {}
        }

        // 6) Respuesta
        return AccessEventPurchaseResponseDTO.builder()
                .purchaseNumber(receipt.getPurchaseNumber())
                .clerkId(user.getClerkId())
                .email(user.getEmail())
                .fullName(user.getFullname())
                .purchaseAt(receipt.getPurchaseAt())
                .landingSlug(slug)
                .planKey(req.getPlanKey())
                .planTitle(planTitle)
                .quantity(quantity)
                .couponCode(req.getCouponCode())
                .currency(currency)
                .unitPriceShown(unitPriceShown)
                .subtotal(subtotal)
                .discount(discount)
                .commission(commission)
                .total(total)
                .emailed(emailed)
                .build();
    }

    // ===== helpers =====
    private static BigDecimal optionalScale(BigDecimal v) {
        return v == null ? null : v.setScale(2, RoundingMode.HALF_UP);
    }
    private static BigDecimal requiredScale(BigDecimal v) {
        if (v == null) throw new IllegalArgumentException("total requerido");
        return v.setScale(2, RoundingMode.HALF_UP);
    }



}