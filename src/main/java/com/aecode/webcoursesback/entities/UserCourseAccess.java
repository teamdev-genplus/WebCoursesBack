package com.aecode.webcoursesback.entities;
import lombok.*;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usercourseaccess")
@SequenceGenerator(name = "access_seq", sequenceName = "access_sequence", allocationSize = 1)

public class UserCourseAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_seq")
    private int accessId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


}
