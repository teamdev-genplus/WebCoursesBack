package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "certificates")
@SequenceGenerator(name = "certificates_seq", sequenceName = "certificates_sequence", allocationSize = 1)
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificates_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 255, nullable = false)
    private String url;
}
