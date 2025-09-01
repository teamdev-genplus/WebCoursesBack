package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "module_resource_links")
@SequenceGenerator(name = "mres_link_seq", sequenceName = "mres_link_sequence", allocationSize = 1)

public class ModuleResourceLink {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mres_link_seq")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(nullable = false, length = 255)
    private String name;      // ej: "Tu pizarra en Miro"

    @Column(nullable = false, length = 1000)
    private String url;       // ej: https://miro.com/board/...

    @Column
    private Integer orderNumber; // para ordenar el dropdown

    @Column(nullable = false)
    private boolean active = true;
}
