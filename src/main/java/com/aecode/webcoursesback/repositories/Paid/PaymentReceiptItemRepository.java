package com.aecode.webcoursesback.repositories.Paid;
import com.aecode.webcoursesback.entities.Paid.PaymentReceiptItem;
import org.springframework.data.jpa.repository.*;

import java.util.List;
public interface PaymentReceiptItemRepository extends JpaRepository<PaymentReceiptItem, Long>{
    List<PaymentReceiptItem> findByReceipt_Id(Long receiptId);
}
