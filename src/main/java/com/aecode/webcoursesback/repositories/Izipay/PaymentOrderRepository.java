package com.aecode.webcoursesback.repositories.Izipay;
import com.aecode.webcoursesback.entities.Izipay.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId);

    // NUEVO: listar por dominio, ordenado por fecha de creación (desc)
    List<PaymentOrder> findAllByDomainOrderByCreatedAtDesc(PaymentOrder.OrderDomain domain);
}
