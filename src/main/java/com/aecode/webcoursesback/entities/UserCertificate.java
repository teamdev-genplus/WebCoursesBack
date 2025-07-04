package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_certificates")
@SequenceGenerator(name = "user_cert_seq", sequenceName = "user_cert_sequence", allocationSize = 1)
public class UserCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_cert_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(length = 255, nullable = false)
    private String certificateName;

    @Column(length = 255, nullable = false)
    private String certificateUrl;
}
