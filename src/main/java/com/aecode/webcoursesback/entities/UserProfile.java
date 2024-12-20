package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userprofiles")
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)

public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private int userId;

    @Column(length = 50)
    private String fullname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(length = 100)
    private String rol = "Estudiante";

    @Column(length = 100)
    private String status = "Activo";

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressSession> userprogresssessions = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressRW> userprogressrw = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCourseAccess> usercourseaccess = new ArrayList<>();

    public UserProfile() {
    }

    public UserProfile(int userId, String fullname, String email, String passwordHash, String rol, String status, List<UserProgressSession> userprogresssessions, List<UserProgressRW> userprogressrw, List<UserCourseAccess> usercourseaccess) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.status = status;
        this.userprogresssessions = userprogresssessions;
        this.userprogressrw = userprogressrw;
        this.usercourseaccess = usercourseaccess;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserProgressSession> getUserprogresssessions() {
        return userprogresssessions;
    }

    public void setUserprogresssessions(List<UserProgressSession> userprogresssessions) {
        this.userprogresssessions = userprogresssessions;
    }

    public List<UserProgressRW> getUserprogressrw() {
        return userprogressrw;
    }

    public void setUserprogressrw(List<UserProgressRW> userprogressrw) {
        this.userprogressrw = userprogressrw;
    }

    public List<UserCourseAccess> getUsercourseaccess() {
        return usercourseaccess;
    }

    public void setUsercourseaccess(List<UserCourseAccess> usercourseaccess) {
        this.usercourseaccess = usercourseaccess;
    }

}
