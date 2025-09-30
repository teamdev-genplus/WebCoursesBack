package com.aecode.webcoursesback.repositories.Paid;

import com.aecode.webcoursesback.entities.Paid.UnifiedPaidOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UnifiedPaidOrderRepository extends JpaRepository<UnifiedPaidOrder, Long> {
    List<UnifiedPaidOrder> findAllByOrderByPaidAtDesc();
    // NUEVO: para evitar duplicados exactos
    Optional<UnifiedPaidOrder> findByEmailAndPaidAtAndModuleIdsCsv(String email, OffsetDateTime paidAt, String moduleIdsCsv);
}
