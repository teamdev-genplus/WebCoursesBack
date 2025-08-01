package com.aecode.webcoursesback.entities;
import lombok.*;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usercourseaccess")
@SequenceGenerator(name = "ucaccess_seq", sequenceName = "ucaccess_sequence", allocationSize = 1)

public class UserCourseAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ucaccess_seq")
    private Long accessId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;
}
