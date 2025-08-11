package com.aecode.webcoursesback.entities.Coupon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon_redemptions")
@SequenceGenerator(name = "coupon_redemption_seq", sequenceName = "coupon_redemption_sequence", allocationSize = 1)
public class CouponRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_redemption_seq")
    private Long redemptionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(nullable = false, length = 255)
    private String clerkId; // ID del usuario (desencriptado)

    @Column(nullable = false)
    private LocalDateTime redeemedAt;
}
