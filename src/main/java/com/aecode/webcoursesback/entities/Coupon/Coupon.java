package com.aecode.webcoursesback.entities.Coupon;

import com.aecode.webcoursesback.entities.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(unique = true, nullable = false, length = 50)
    private String code; // Código único del cupón (ejemplo: PROMO20)

    @Column(length = 255)
    private String description;

    // Descuento: puede ser porcentaje o monto fijo
    @Column
    private Double discountPercentage; // Si no es null, aplica porcentaje

    @Column
    private Double discountAmount; // Si no es null, aplica monto fijo

    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;

    // Límite de usos global para este cupón
    @Column
    private Integer usageLimit;

    // Contador de usos actual
    @Column
    private Integer usageCount = 0;

    // Si true, un usuario solo puede usar el cupón una vez
    @Column
    private Boolean singleUsePerUser;

    // Si true, aplica solo para cursos específicos (si no, es general)
    @Column
    private Boolean courseSpecific;

    @Column
    private Boolean active = true;  // Por defecto activo


    // Cursos a los que aplica (si courseSpecific == true)
    @ManyToMany
    @JoinTable(name = "coupon_courses",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> applicableCourses;

    // ===== NUEVO: cupones de landing específica =====
    // NUEVO: especificidad para landing
    @Column private Boolean landingSpecific;        // true = aplica sólo a una landing
    @Column(length = 150) private String landingSlug;              // requerido si landingSpecific = true

}
