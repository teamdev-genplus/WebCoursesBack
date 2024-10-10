package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "userprofile")
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

    @Column(nullable = false)
    private boolean hasAccess = false;

    public UserProfile() {
    }

    public UserProfile(int userId, String fullname, String email, String passwordHash, boolean hasAccess) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.hasAccess = hasAccess;
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

    public boolean isHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }
}
