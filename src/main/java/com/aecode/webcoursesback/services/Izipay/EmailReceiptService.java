package com.aecode.webcoursesback.services.Izipay;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserProfile;

import java.time.OffsetDateTime;
import java.util.List;
public interface EmailReceiptService {
    /**
     * Envía el correo de confirmación con el MISMO diseño HTML del endpoint de PayPal,
     * ajustado para el proveedor "Izipay".
     *
     * @param user           usuario (email/nombre)
     * @param modules        módulos comprados
     * @param purchaseNumber número de compra (usa orderId de Izipay)
     * @param purchasedAt    fecha de compra (usa grantedAt de la orden, o now)
     * @param currency       código de moneda (ej: "PEN" o "USD")
     * @param amountPaid     monto total pagado (double, ej: order.amountCents / 100.0)
     */
    void sendIzipayReceipt(UserProfile user,
                           List<Module> modules,
                           String purchaseNumber,
                           OffsetDateTime purchasedAt,
                           String currency,
                           double amountPaid);
    /* ===== NUEVO: recibo para EVENT/LANDING con mismo template base ===== */
    void sendIzipayEventReceipt(UserProfile user,
                                String eventTitle,
                                String planTitle,
                                int quantity,
                                String currency,
                                double unitPriceShown,   // priceAmount (lo que se muestra como unitario)
                                double subtotal,         // selected.getSubtotal()
                                double discountTotal,    // selected.getDiscountTotal()
                                double total,            // selected.getTotal()
                                String purchaseNumber,
                                OffsetDateTime purchasedAt);

}
