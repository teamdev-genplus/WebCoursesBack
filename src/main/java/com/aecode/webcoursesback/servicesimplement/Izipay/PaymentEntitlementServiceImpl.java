package com.aecode.webcoursesback.servicesimplement.Izipay;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrder;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrderItem;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.repositories.Izipay.PaymentOrderItemRepository;
import com.aecode.webcoursesback.repositories.Izipay.PaymentOrderRepository;
import com.aecode.webcoursesback.services.IUserAccessService;
import com.aecode.webcoursesback.services.Izipay.EmailReceiptService;
import com.aecode.webcoursesback.services.Izipay.PaymentEntitlementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentEntitlementServiceImpl implements PaymentEntitlementService {
    private final PaymentOrderItemRepository itemRepo;
    private final PaymentOrderRepository orderRepo;
    private final IUserAccessService userAccessService;

    // NUEVO: para armar correo
    private final EmailReceiptService emailReceiptService;
    private final IModuleRepo moduleRepo;
    private final IUserProfileRepository userProfileRepo;

    @Override
    @Transactional
    public void fulfillIfPaid(PaymentOrder order) {
        // Sólo para órdenes pagadas y no concedidas
        if (order == null || order.getStatus() != PaymentOrder.PaymentStatus.PAID) return;
        if (order.isEntitlementsGranted()) return;

        // Validamos datos mínimos
        if (order.getClerkId() == null || order.getClerkId().isBlank()) {
            throw new EntityNotFoundException("Orden sin clerkId; no se puede conceder acceso");
        }

        // Cargamos items (módulos)
        List<PaymentOrderItem> items = itemRepo.findByOrder(order);
        if (items.isEmpty()) {
            // No hay qué conceder; marcamos concedido para no intentar indefinidamente
            order.setEntitlementsGranted(true);
            order.setGrantedAt(OffsetDateTime.now());
            orderRepo.save(order);
            return;
        }

        // Tomamos los módulos a conceder
        List<Long> moduleIds = items.stream()
                .map(PaymentOrderItem::getModuleId)
                .distinct()
                .collect(Collectors.toList());

        // 1) Concedemos accesos
        userAccessService.grantMultipleModuleAccess(order.getClerkId(), moduleIds);

        // 2) Enviamos email con el MISMO diseño
        try {
            UserProfile user = userProfileRepo.findByClerkId(order.getClerkId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado para clerkId: " + order.getClerkId()));

            List<Module> modules = moduleRepo.findAllById(moduleIds);

            double amountPaid = (order.getAmountCents() == null ? 0 : order.getAmountCents()) / 100.0;

            emailReceiptService.sendIzipayReceipt(
                    user,
                    modules,
                    order.getOrderId(),            // Nro de compra
                    OffsetDateTime.now(),          // o order.getGrantedAt() si quieres la marca exacta
                    order.getCurrency(),           // "PEN" / "USD"
                    amountPaid
            );

        } catch (Exception e) {
            // No bloquear el flujo por el email, sólo registrar
            System.err.println("Fallo preparando o enviando email Izipay: " + e.getMessage());
        }

        // 3) Marcamos concedido (idempotencia)
        order.setEntitlementsGranted(true);
        order.setGrantedAt(OffsetDateTime.now());
        orderRepo.save(order);
    }
}
