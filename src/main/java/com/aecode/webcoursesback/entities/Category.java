package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "categories")
@SequenceGenerator(name = "category_seq", sequenceName = "category_sequence", allocationSize = 1)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    private Long categoryId;

    @Column(nullable = false, unique = true, length = 120)
    private String name; // ej: "Revit", "Dynamo", "IA"
}
