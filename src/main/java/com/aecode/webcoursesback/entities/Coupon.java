package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupons")
@SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_sequence", allocationSize = 1)
public class Coupon {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
    private Long couponId;

    @Column(length = 255)
    private String code;

    @Column(nullable = false)
    private boolean isGeneral = false;

    @Column(length = 255)
    private int discount;

    @Column(nullable = false)
    private boolean active = true;

    @Column
    private LocalDate expirationDate;

    @ManyToMany
    @JoinTable(name = "module_coupons",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id"))
    private List<Module> module = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_coupons",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> course = new ArrayList<>();

}
