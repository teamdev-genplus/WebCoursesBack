package com.aecode.webcoursesback.servicesimplement.Izipay;
import com.aecode.webcoursesback.config.IzipayProperties;
import com.aecode.webcoursesback.dtos.Izipay.FormTokenCreateRequest;
import com.aecode.webcoursesback.dtos.Izipay.FormTokenCreateResponse;
import com.aecode.webcoursesback.dtos.Izipay.ValidatePaymentRequest;
import com.aecode.webcoursesback.dtos.Izipay.ValidatePaymentResponse;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrder;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrderItem;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.integrations.IzipayClient;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.repositories.Izipay.PaymentOrderItemRepository;
import com.aecode.webcoursesback.repositories.Izipay.PaymentOrderRepository;
import com.aecode.webcoursesback.services.Izipay.PaymentEntitlementService;
import com.aecode.webcoursesback.services.Izipay.PaymentService;
import com.aecode.webcoursesback.services.Paid.UnifiedPaidOrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final IUserProfileRepository userProfileRepository;
    private final IzipayClient izipayClient;
    private final IzipayProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    // NUEVO
    private final PaymentOrderItemRepository itemRepo;
    private final PaymentEntitlementService entitlementService;

    // NUEVO
    private final UnifiedPaidOrderService unifiedPaidOrderService;


    @Override
    public FormTokenCreateResponse createFormToken(FormTokenCreateRequest req) {
        // 1) Validaciones mínimas
        if (req.getOrderId() == null || req.getOrderId().isBlank())
            throw new IllegalArgumentException("orderId requerido");
        if (req.getAmountCents() == null || req.getAmountCents() <= 0)
            throw new IllegalArgumentException("amountCents inválido");
        if (req.getCurrency() == null || req.getCurrency().isBlank())
            throw new IllegalArgumentException("currency requerido");

        // 2) Resolver email por clerkId si no llega
        String email = req.getEmail();
        if ((email == null || email.isBlank()) && req.getClerkId() != null && !req.getClerkId().isBlank()) {
            userProfileRepository.findByClerkId(req.getClerkId())
                    .map(UserProfile::getEmail)
                    .ifPresent(e -> req.setEmail(e));
        }

        // 3) Crear/actualizar PaymentOrder (PENDING)
        PaymentOrder order = paymentOrderRepository.findByOrderId(req.getOrderId())
                .orElseGet(PaymentOrder::new);

        order.setOrderId(req.getOrderId());
        order.setClerkId(req.getClerkId());
        order.setEmail(req.getEmail());
        order.setAmountCents(req.getAmountCents());
        order.setCurrency(req.getCurrency());
        order.setStatus(PaymentOrder.PaymentStatus.PENDING);
        order.setMode(resolveModeFromPassword(props.getPassword()));
        order.setEntitlementsGranted(false);
        order.setGrantedAt(null);
        order = paymentOrderRepository.save(order);
        // 3b) Guardar líneas de orden (módulos)
        itemRepo.deleteByOrder(order);
        if (req.getModuleIds() != null && !req.getModuleIds().isEmpty()) {
            for (Long mid : req.getModuleIds()) {
                PaymentOrderItem it = PaymentOrderItem.builder()
                        .order(order)
                        .moduleId(mid)
                        .priceCents(null) // opcional
                        .build();
                itemRepo.save(it);
            }
        }
        // 4) Armar payload para CreatePayment
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", order.getAmountCents());
        payload.put("currency", order.getCurrency());
        payload.put("orderId", order.getOrderId());

        Map<String, Object> customer = new HashMap<>();
        if (order.getEmail() != null) customer.put("email", order.getEmail());
        Map<String, Object> billing = new HashMap<>();
        if (req.getFirstName() != null) billing.put("firstName", req.getFirstName());
        if (req.getLastName() != null)  billing.put("lastName",  req.getLastName());
        if (req.getPhoneNumber() != null) billing.put("phoneNumber", req.getPhoneNumber());
        if (req.getIdentityType() != null) billing.put("identityDocumentType", req.getIdentityType());
        if (req.getIdentityCode() != null) billing.put("identityDocumentNumber", req.getIdentityCode());
        if (req.getAddress() != null) billing.put("address", req.getAddress());
        if (req.getCountry() != null) billing.put("country", req.getCountry());
        if (req.getCity() != null)    billing.put("city", req.getCity());
        if (req.getState() != null)   billing.put("state", req.getState());
        if (req.getZipCode() != null) billing.put("zipCode", req.getZipCode());
        if (!billing.isEmpty()) customer.put("billingDetails", billing);
        if (!customer.isEmpty()) payload.put("customer", customer);

        // 5) Llamar a Izipay -> formToken
        String formToken = izipayClient.createPaymentAndGetFormToken(payload);

        // 6) Guardar formToken
        order.setFormToken(formToken);
        paymentOrderRepository.save(order);

        // 7) Devolver formToken + publicKey para que el front dibuje la pasarela
        return FormTokenCreateResponse.builder()
                .formToken(formToken)
                .publicKey(izipayClient.getPublicKey())
                .build();
    }

    @Override
    public ValidatePaymentResponse validateBrowserReturn(ValidatePaymentRequest req) {
        if (req.getKrAnswer() == null || req.getKrHash() == null)
            throw new IllegalArgumentException("kr-answer y kr-hash requeridos");

        // Validar firma con HMAC_SHA256 (retorno del navegador)
        boolean ok = checkHash(req.getKrHash(), izipayClient.getHmacSha256(), req.getKrAnswer());
        if (!ok) {
            return ValidatePaymentResponse.builder().valid(false).build();
        }

        // Parsear kr-answer
        String orderId = null;
        String orderStatus = null;
        try {
            JsonNode root = mapper.readTree(req.getKrAnswer());
            JsonNode details = root.path("orderDetails");
            if (details.isObject()) {
                JsonNode oid = details.get("orderId");
                if (oid != null) orderId = oid.asText();
            }
            JsonNode statusNode = root.get("orderStatus");
            if (statusNode != null) orderStatus = statusNode.asText();
        } catch (Exception e) {
            // no rompas, pero marca no válido
            return ValidatePaymentResponse.builder().valid(false).build();
        }

        // Actualizar estado del pedido
        if (orderId != null) {
            var opt = paymentOrderRepository.findByOrderId(orderId);
            if (opt.isPresent()) {
                PaymentOrder po = opt.get();
                PaymentOrder.PaymentStatus newStatus = mapOrderStatus(orderStatus);
                po.setStatus(newStatus);
                paymentOrderRepository.save(po);

                // Si quedó pagado, conceder accesos (idempotente) + LOG UNIFICADO
                if (newStatus == PaymentOrder.PaymentStatus.PAID) {
                    entitlementService.fulfillIfPaid(po);

                    // ---- Registrar en la tabla unificada ----
                    try {
                        var items = itemRepo.findByOrder(po);
                        var moduleIds = items.stream()
                                .map(PaymentOrderItem::getModuleId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList();

                        if (!moduleIds.isEmpty()) {
                            String fullName = userProfileRepository.findByClerkId(po.getClerkId())
                                    .map(UserProfile::getFullname)
                                    .orElse("");

                            unifiedPaidOrderService.logPaidOrder(
                                    Optional.ofNullable(po.getEmail()).orElse(""),
                                    fullName,
                                    java.time.OffsetDateTime.now(), // si no tienes fecha exacta del PSP aquí
                                    moduleIds
                            );
                        }
                    } catch (Exception ignore) { /* log si quieres */ }
                    // -----------------------------------------
                }
            }
        }

        return ValidatePaymentResponse.builder()
                .valid(true)
                .orderId(orderId)
                .orderStatus(orderStatus)
                .build();
    }

    @Override
    public String handleIpn(Map<String, String> formParams) {
        // Izipay envía x-www-form-urlencoded con kr-answer & kr-hash
        String krHash = formParams.get("kr-hash");
        String krAnswer = formParams.get("kr-answer");
        if (krHash == null || krAnswer == null) return "No valid IPN";

        // Validar firma con PASSWORD (clave API REST) para IPN
        boolean ok = checkHash(krHash, izipayClient.getPassword(), krAnswer);
        if (!ok) return "No valid IPN";

        // Leer orderId y orderStatus y actualizar
        try {
            JsonNode root = mapper.readTree(krAnswer);
            String orderStatus = optText(root.get("orderStatus"));
            JsonNode details = root.get("orderDetails");
            String orderId = (details != null) ? optText(details.get("orderId")) : null;

            if (orderId != null) {
                paymentOrderRepository.findByOrderId(orderId).ifPresent(po -> {
                    PaymentOrder.PaymentStatus newStatus = mapOrderStatus(orderStatus);
                    po.setStatus(newStatus);
                    paymentOrderRepository.save(po);

                    // Si quedó pagado, conceder accesos (idempotente) + LOG UNIFICADO
                    if (newStatus == PaymentOrder.PaymentStatus.PAID) {
                        entitlementService.fulfillIfPaid(po);

                        // ---- Registrar en la tabla unificada ----
                        try {
                            var items = itemRepo.findByOrder(po);
                            var moduleIds = items.stream()
                                    .map(PaymentOrderItem::getModuleId)
                                    .filter(Objects::nonNull)
                                    .distinct()
                                    .toList();

                            if (!moduleIds.isEmpty()) {
                                String fullName = userProfileRepository.findByClerkId(po.getClerkId())
                                        .map(UserProfile::getFullname)
                                        .orElse("");

                                unifiedPaidOrderService.logPaidOrder(
                                        Optional.ofNullable(po.getEmail()).orElse(""),
                                        fullName,
                                        java.time.OffsetDateTime.now(), // o una fecha del payload si la parseas
                                        moduleIds
                                );
                            }
                        } catch (Exception ignore) { /* log si quieres */ }
                        // -----------------------------------------
                    }
                });
            }
        } catch (Exception ignored) {}

        // Responder 200/OK con cuerpo "OK" (Izipay lo requiere)
        return "OK";
    }


    // ================= Helpers =================

    private String resolveModeFromPassword(String password) {
        if (password == null) return null;
        return password.startsWith("testpassword") ? "TEST" :
                password.startsWith("prodpassword") ? "PROD" : null;
    }

    private PaymentOrder.PaymentStatus mapOrderStatus(String orderStatus) {
        if (orderStatus == null) return PaymentOrder.PaymentStatus.UNPAID;
        return switch (orderStatus.toUpperCase(Locale.ROOT)) {
            case "PAID"   -> PaymentOrder.PaymentStatus.PAID;
            case "UNPAID" -> PaymentOrder.PaymentStatus.UNPAID;
            default       -> PaymentOrder.PaymentStatus.UNPAID;
        };
    }

    private boolean checkHash(String receivedHash, String key, String krAnswer) {
        try {
            // Izipay recomienda reemplazar '\/' por '/' para firmar
            String normalized = krAnswer.replace("\\/", "/");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(normalized.getBytes(StandardCharsets.UTF_8));
            String calc = Hex.encodeHexString(raw);
            return calc.equals(receivedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String optText(JsonNode n) {
        return (n == null || n.isNull()) ? null : n.asText();
    }
}
