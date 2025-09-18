package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_certificates",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_cert_certificate_code", columnNames = {"certificate_code"})
        })
@SequenceGenerator(name = "user_cert_seq", sequenceName = "user_cert_sequence", allocationSize = 1)
public class UserCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_cert_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(length = 255, nullable = false)
    private String certificateName;

    @Column(length = 255)
    private String certificateImage;

    @Column(length = 255, nullable = false)
    private String certificateUrl;

    @Column(name = "certificate_code", length = 64, nullable = false)
    private String certificateCode; // código visible/validable (ej. AEC-2025-0001)

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt; // fecha de emisión
}
