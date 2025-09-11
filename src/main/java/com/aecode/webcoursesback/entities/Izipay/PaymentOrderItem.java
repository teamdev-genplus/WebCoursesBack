package com.aecode.webcoursesback.entities.Izipay;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "payment_order_items", indexes = {
        @Index(name = "idx_pay_items_order_id", columnList = "order_id")
})
@SequenceGenerator(name = "pay_item_seq", sequenceName = "payment_order_item_seq", allocationSize = 1)

public class PaymentOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_item_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id_fk", nullable = false)
    private PaymentOrder order;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "price_cents")
    private Integer priceCents;
}
