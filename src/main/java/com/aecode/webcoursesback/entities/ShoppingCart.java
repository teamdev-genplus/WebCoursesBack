package com.aecode.webcoursesback.entities;
import lombok.*;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shoppingcarts")
@SequenceGenerator(name = "cart_seq", sequenceName = "cart_sequence", allocationSize = 1)
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_seq")
    private int cartId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "seccourse_id", nullable = false)
    private SecondaryCourses secondaryCourse;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}
