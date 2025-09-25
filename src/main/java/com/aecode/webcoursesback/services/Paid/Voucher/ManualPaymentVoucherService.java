package com.aecode.webcoursesback.services.Paid.Voucher;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;

public interface ManualPaymentVoucherService {

    /**
     * Sube el voucher (opcional) + datos libres. Ningún campo es obligatorio.
     * Si vienen clerkId y moduleIds, se puede advertir si hay módulos repetidos,
     * pero la validación dura se hace al validar().
     */
    ManualPaymentVoucherDTO uploadVoucher(
            MultipartFile voucherFileOrNull,
            String clerkIdOrNull,
            List<Long> moduleIdsOrNull,
            String paymentMethodOrNull,
            OffsetDateTime paidAtOrNull,
            String statusOrNull // "PENDING"|"PAID"
    );

    /**
     * Cambia el booleano 'validated'. Si pasa a true, verifica clerkId, módulos no repetidos,
     * y crea el registro en UnifiedPaidOrder.
     */
    ManualPaymentVoucherDTO setValidated(Long id, boolean validated);

    /** Lista para admin */
    List<ManualPaymentVoucherDTO> listAll();
}