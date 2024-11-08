package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "userprogressrw")
@SequenceGenerator(name = "progressrw_seq", sequenceName = "progressrw_sequence", allocationSize = 1)

public class UserProgressRW {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "progressrw_seq")

    private int progressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false)
    private RelatedWork rw;

    @Column(nullable = false)
    private boolean isCompleted;

    public UserProgressRW() {
    }

    public UserProgressRW(int progressId, UserProfile userProfile, RelatedWork rw, boolean isCompleted) {
        this.progressId = progressId;
        this.userProfile = userProfile;
        this.rw = rw;
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

    public RelatedWork getRw() {
        return rw;
    }

    public void setRw(RelatedWork rw) {
        this.rw = rw;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
