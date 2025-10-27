package com.aecode.webcoursesback.repositories.Paid.Voucher;
import com.aecode.webcoursesback.entities.Paid.Voucher.ManualPaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ManualPaymentVoucherRepository extends JpaRepository<ManualPaymentVoucher, Long> {
    // Solo EVENT o solo MODULES
    List<ManualPaymentVoucher> findByDomainOrderByCreatedAtDesc(ManualPaymentVoucher.PaymentDomain domain);

    // Registros antiguos sin dominio
    List<ManualPaymentVoucher> findByDomainIsNullOrderByCreatedAtDesc();
}