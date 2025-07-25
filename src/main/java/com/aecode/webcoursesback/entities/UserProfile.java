package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "userprofiles")
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)

public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long userId;

    //se manejara como un texto
    @Column(columnDefinition = "TEXT")
    private String clerkId;

    @Column(length = 50)
    private String fullname;
    //abcd
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(length = 100)
    private String rol = "user";

    @Column(length = 100)
    private String status = "Activo";

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserDetail userDetail;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCourseAccess> usercourseaccess = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserModuleAccess> usermoduleaccess = new ArrayList<>();


}
