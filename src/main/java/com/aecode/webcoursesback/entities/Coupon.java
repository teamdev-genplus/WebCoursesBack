package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "coupons")
@SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_sequence", allocationSize = 1)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
    private int couponId;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private int discount;

    @ManyToOne
    @JoinColumn(name = "seccourse_id", nullable = false)
    private SecondaryCourses secondary_course;

    public Coupon() {
    }

    public Coupon(int couponId, String name, int discount, SecondaryCourses secondary_course) {
        this.couponId = couponId;
        this.name = name;
        this.discount = discount;
        this.secondary_course = secondary_course;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public SecondaryCourses getSecondary_course() {
        return secondary_course;
    }

    public void setSecondary_course(SecondaryCourses secondary_course) {
        this.secondary_course = secondary_course;
    }
}
