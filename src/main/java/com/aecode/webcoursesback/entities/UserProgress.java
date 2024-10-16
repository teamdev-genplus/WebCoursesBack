package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int progressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class classes;

    @Column(nullable = false)
    private boolean isCompleted;

    public UserProgress() {
    }

    public UserProgress(int progressId, UserProfile userProfile, Class classes, boolean isCompleted) {
        this.progressId = progressId;
        this.userProfile = userProfile;
        this.classes = classes;
        this.isCompleted = isCompleted;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Class getClasses() {
        return classes;
    }

    public void setClasses(Class classes) {
        this.classes = classes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
