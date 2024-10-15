package com.aecode.webcoursesback.dtos;

import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.UserProfile;


public class UserProgressDTO {
    private int progressId;
    private UserProfile userProfile;
    private Class classes;
    private boolean isCompleted;

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
