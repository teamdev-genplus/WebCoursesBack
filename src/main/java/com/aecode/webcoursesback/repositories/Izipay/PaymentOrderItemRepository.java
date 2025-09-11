package com.aecode.webcoursesback.repositories.Izipay;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrder;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentOrderItemRepository extends JpaRepository<PaymentOrderItem, Long> {
    List<PaymentOrderItem> findByOrder(PaymentOrder order);
    void deleteByOrder(PaymentOrder order);
}
