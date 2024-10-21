package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userprofiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(length = 50)
    private String fullname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressSession> progressSessions = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressUnit> progressUnits = new ArrayList<>();

    public UserProfile() {
    }

    public UserProfile(int userId, String fullname, String email, String passwordHash, List<UserProgressSession> progressSessions, List<UserProgressUnit> progressUnits) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.progressSessions = progressSessions;
        this.progressUnits = progressUnits;
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

    public List<UserProgressSession> getProgressSessions() {
        return progressSessions;
    }

    public void setProgressSessions(List<UserProgressSession> progressSessions) {
        this.progressSessions = progressSessions;
    }

    public List<UserProgressUnit> getProgressUnits() {
        return progressUnits;
    }

    public void setProgressUnits(List<UserProgressUnit> progressUnits) {
        this.progressUnits = progressUnits;
    }
}
