package com.aecode.webcoursesback.repositories.Paid.Voucher;
import com.aecode.webcoursesback.entities.Paid.Voucher.ManualPaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualPaymentVoucherRepository extends JpaRepository<ManualPaymentVoucher, Long> {
}