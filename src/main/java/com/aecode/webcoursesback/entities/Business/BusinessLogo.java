package com.aecode.webcoursesback.entities.Business;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "business_logos")
public class BusinessLogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** "Google" */
    @Column(nullable = false, length = 120)
    private String name;

    /** URL de imagen del logo */
    @Column(nullable = false, length = 500)
    private String logoUrl;

    /** URL opcional al sitio */
    @Column(length = 500)
    private String websiteUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
