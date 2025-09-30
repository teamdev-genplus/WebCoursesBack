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
import java.util.Objects;
import java.util.Set;
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
    public GrantResult fulfillIfPaid(PaymentOrder order) {
        // Solo órdenes pagadas y aún no concedidas
        if (order == null || order.getStatus() != PaymentOrder.PaymentStatus.PAID) {
            return new GrantResult(List.of(), List.of());
        }
        if (order.isEntitlementsGranted()) {
            return new GrantResult(List.of(), List.of());
        }

        if (order.getClerkId() == null || order.getClerkId().isBlank()) {
            throw new EntityNotFoundException("Orden sin clerkId; no se puede conceder acceso");
        }

        // Items de la orden
        List<PaymentOrderItem> items = itemRepo.findByOrder(order);
        if (items.isEmpty()) {
            order.setEntitlementsGranted(true);
            order.setGrantedAt(OffsetDateTime.now());
            orderRepo.save(order);
            return new GrantResult(List.of(), List.of());
        }

        // Distinct módulos de la orden
        List<Long> requestedIds = items.stream()
                .map(PaymentOrderItem::getModuleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Conjunto de módulos ya poseídos
        Set<Long> owned = userAccessService.getUserModulesByClerkId(order.getClerkId()).stream()
                .map(m -> m.getModuleId())
                .collect(Collectors.toSet());

        // Particionar
        List<Long> toGrant = requestedIds.stream().filter(id -> !owned.contains(id)).toList();
        List<Long> skipped = requestedIds.stream().filter(owned::contains).toList();

        // Conceder solo los nuevos
        if (!toGrant.isEmpty()) {
            userAccessService.grantMultipleModuleAccess(order.getClerkId(), toGrant);

            // Email
            try {
                UserProfile user = userProfileRepo.findByClerkId(order.getClerkId())
                        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado para clerkId: " + order.getClerkId()));
                List<Module> modules = moduleRepo.findAllById(toGrant);
                double amountPaid = (order.getAmountCents() == null ? 0 : order.getAmountCents()) / 100.0;

                emailReceiptService.sendIzipayReceipt(
                        user,
                        modules,
                        order.getOrderId(),
                        OffsetDateTime.now(),
                        order.getCurrency(),
                        amountPaid
                );
            } catch (Exception e) {
                System.err.println("Fallo preparando/enviando email Izipay: " + e.getMessage());
            }
        }

        // Marcar concedido SIEMPRE (para no reintentar), aunque todo haya sido duplicado
        order.setEntitlementsGranted(true);
        order.setGrantedAt(OffsetDateTime.now());
        orderRepo.save(order);

        return new GrantResult(toGrant, skipped);
    }
}
