package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseProduct {

    @Column(length = 255)
    private String brochureUrl;

    @Column(length = 255)
    private String whatsappGroupLink;

    @Column
    private LocalDate startDate;


    @Column(length = 100)
    private String urlName;

    @Column
    private Double priceRegular;

    @Column
    private Double discountPercentage;

    @Column
    private Double promptPaymentPrice;

    @Column
    private Boolean isOnSale;

    @Column(columnDefinition = "TEXT")
    private String achievement;

    @Column(length = 255)
    private String urlsyllabus;

    @Column(length = 255)
    private String urlJoinClass;

}
