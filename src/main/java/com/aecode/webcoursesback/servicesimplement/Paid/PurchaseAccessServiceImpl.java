package com.aecode.webcoursesback.servicesimplement.Paid;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseResponseDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Paid.EmailReceiptRenderer;
import com.aecode.webcoursesback.entities.Paid.PaymentReceipt;
import com.aecode.webcoursesback.entities.Paid.PaymentReceiptItem;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.repositories.Paid.PaymentReceiptItemRepository;
import com.aecode.webcoursesback.repositories.Paid.PaymentReceiptRepository;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.IUserAccessService;
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

        // 2) Validar módulos
        List<Module> modules = moduleRepo.findAllById(req.getModuleIds());
        if (modules.size() != req.getModuleIds().size()) {
            throw new EntityNotFoundException("Uno o más módulos no existen.");
        }

        // ===== subtotal efectivo (req o cálculo de módulos)
        BigDecimal effectiveSubtotal = Optional.ofNullable(req.getSubtotal())
                .orElseGet(() -> subtotalFromModules(modules));

        // ===== comisión efectiva (0 si no llega)
        BigDecimal effectiveCommission = Optional.ofNullable(req.getCommission())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        // ===== descuento efectivo:
        // Si no llega => descuento = subtotal + comisión - total (>=0)
        BigDecimal effectiveDiscount = Optional.ofNullable(req.getDiscount())
                .orElseGet(() -> {
                    BigDecimal d = effectiveSubtotal.add(effectiveCommission).subtract(req.getTotal());
                    if (d.compareTo(BigDecimal.ZERO) < 0) d = BigDecimal.ZERO;
                    return d;
                })
                .setScale(2, RoundingMode.HALF_UP);

        // 3) Buscar o crear recibo (idempotencia por purchaseNumber)
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
                            .moduleIdsCsv(req.getModuleIds().stream().map(String::valueOf).collect(Collectors.joining(",")))
                            .entitlementsGranted(false)
                            .build();
                    return receiptRepo.save(r);
                });

        // 4) Insertar items si es nuevo (o asegurar items existan)
        if (receiptItemRepo.findByReceipt_Id(receipt.getId()).isEmpty()) {
            List<PaymentReceiptItem> items = new ArrayList<>();
            for (Module m : modules) {
                items.add(PaymentReceiptItem.builder()
                        .receipt(receipt)
                        .moduleId(m.getModuleId())
                        .courseTitle(m.getCourse() != null ? m.getCourse().getTitle() : null)
                        .moduleTitle(m.getProgramTitle())
                        .build());
            }
            receiptItemRepo.saveAll(items);
        }

        // 5) Conceder accesos (race-safe con markGrantedIfNotGranted)
        int updated = receiptRepo.markGrantedIfNotGranted(receipt.getId());
        List<UserModuleDTO> granted;
        if (updated == 1) {
            // Primera vez que concedemos
            granted = userAccessService.grantMultipleModuleAccess(req.getClerkId(), req.getModuleIds());

            // 6) Enviar emails (usuario + empresa). Si falla, no hay rollback de accesos
            try {
                String html = EmailReceiptRenderer.renderLegacyExact(user, modules, receipt);
                emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", html);

                String plain = EmailReceiptRenderer.renderCompanyPlain(user, modules, receipt);
                emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra (front-asserted)", plain);
            } catch (MessagingException me) {
                // loggear
            }

            // 7) Registrar en la tabla unificada (solo lo esencial)
            try {
                unifiedPaidOrderService.logPaidOrder(
                        user.getEmail(),
                        Optional.ofNullable(user.getFullname()).orElse(""),
                        receipt.getPurchaseAt(), // fecha del recibo
                        modules.stream().map(Module::getModuleId).distinct().toList()
                );
            } catch (Exception ignore) { /* log si quieres */ }

        } else {
            // Ya concedido antes (idempotente)
            granted = userAccessService.getUserModulesByClerkId(req.getClerkId()).stream()
                    .filter(dto -> req.getModuleIds().contains(dto.getModuleId()))
                    .toList();
        }

        return AccessPurchaseResponseDTO.builder()
                .purchaseNumber(receipt.getPurchaseNumber())
                .clerkId(user.getClerkId())
                .email(user.getEmail())
                .fullName(user.getFullname())
                .purchaseAt(receipt.getPurchaseAt())
                .grantedModules(granted)
                .build();
    }

}