package com.aecode.webcoursesback.repositories.Paid;

import com.aecode.webcoursesback.entities.Paid.UnifiedPaidOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnifiedPaidOrderRepository extends JpaRepository<UnifiedPaidOrder, Long> {
    List<UnifiedPaidOrder> findAllByOrderByPaidAtDesc();
}