package com.aecode.webcoursesback.services.Paid.Voucher;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherDTO;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherPayload;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;

public interface ManualPaymentVoucherService {

    /** Sube (opcionalmente) el voucher y persiste metadatos. Nada es obligatorio. */
    ManualPaymentVoucherDTO uploadVoucher(MultipartFile voucherFileOrNull,
                                          ManualPaymentVoucherPayload payload);

    /** Cambia el booleano 'validated'. Si pasa a true:
     * - Si domain=EVENT => no exporta módulos, solo marca validado e informa
     * - Si domain=MODULES/null => exporta a unificada los módulos existentes y no poseídos
     */
    ManualPaymentVoucherDTO setValidated(Long id, boolean validated);

    /** Lista para admin */
    List<ManualPaymentVoucherDTO> listAll();
}