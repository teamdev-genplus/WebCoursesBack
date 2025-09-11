package com.aecode.webcoursesback.repositories.Paid;
import com.aecode.webcoursesback.entities.Paid.PaymentReceipt;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long>{
    Optional<PaymentReceipt> findByPurchaseNumber(String purchaseNumber);

    @Modifying
    @Query("""
      UPDATE PaymentReceipt r SET r.entitlementsGranted = true, r.grantedAt = CURRENT_TIMESTAMP
      WHERE r.id = :id AND r.entitlementsGranted = false
    """)
    int markGrantedIfNotGranted(@Param("id") Long id);
}
