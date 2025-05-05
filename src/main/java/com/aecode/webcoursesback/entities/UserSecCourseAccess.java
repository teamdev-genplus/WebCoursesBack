package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

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

    public UserSecCourseAccess() {
    }

    public UserSecCourseAccess(int accessId, UserProfile userProfile, SecondaryCourses seccourse) {
        this.accessId = accessId;
        this.userProfile = userProfile;
        this.seccourse = seccourse;
    }

    public int getAccessId() {
        return accessId;
    }

    public void setAccessId(int accessId) {
        this.accessId = accessId;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public SecondaryCourses getSeccourse() {
        return seccourse;
    }

    public void setSeccourse(SecondaryCourses seccourse) {
        this.seccourse = seccourse;
    }
}
