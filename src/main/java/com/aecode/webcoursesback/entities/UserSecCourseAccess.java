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
@Table(name = "userseccourseaccess")
@SequenceGenerator(name = "secaccess_seq", sequenceName = "secaccess_sequence", allocationSize = 1)
public class UserSecCourseAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "secaccess_seq")
    private int accessId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private SecondaryCourses seccourse;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

}
