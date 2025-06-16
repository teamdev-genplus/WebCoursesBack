package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tools")
@SequenceGenerator(name = "tool_seq", sequenceName = "tool_sequence", allocationSize = 1)
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_seq")
    private int toolId;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String picture;

}