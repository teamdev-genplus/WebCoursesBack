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
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                            .subtotal(req.getSubtotal()) // opcional; si viene del front, se usa en el email
                            .discount(Optional.ofNullable(req.getDiscount()).orElse(BigDecimal.ZERO))
                            .commission(Optional.ofNullable(req.getCommission()).orElse(BigDecimal.ZERO))
                            .total(req.getTotal())
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

            // 6) Enviar emails (usuario + empresa). Si falla el correo, los accesos ya quedaron.
            // 6) Enviar emails (usuario + empresa). Si falla el correo, los accesos ya quedaron.
            try {
                // ⬇️ ANTES:
                // String html = EmailReceiptRenderer.renderFrontAsserted(user, modules, receipt);

                // ⬇️ AHORA (mismo diseño que tu endpoint antiguo):
                String html = EmailReceiptRenderer.renderLegacyExact(user, modules, receipt);

                emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", html);

                String plain = EmailReceiptRenderer.renderCompanyPlain(user, modules, receipt);
                emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra (front-asserted)", plain);
            } catch (MessagingException me) {
                // Loggear; no hacer rollback de accesos
            }

        } else {
            // Ya concedido antes (idempotente)
            granted = userAccessService.getUserModulesByClerkId(req.getClegitId()).stream()
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