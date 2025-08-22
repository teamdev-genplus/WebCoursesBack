package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usermoduleaccess")
@SequenceGenerator(name = "accessmod_seq", sequenceName = "accessmod_sequence", allocationSize = 1)
public class UserModuleAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accessmod_seq")
    private Long accessId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private boolean completed = false;
}
