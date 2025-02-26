package com.aecode.webcoursesback.dtos;

public class CouponDTO {
    private int couponId;
    private Long seccourseId;
    private String name;
    private int discount;

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public Long getSeccourseId() {
        return seccourseId;
    }

    public void setSeccourseId(Long seccourseId) {
        this.seccourseId = seccourseId;
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
}
