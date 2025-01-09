package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "usermoduleaccess")
@SequenceGenerator(name = "accessmod_seq", sequenceName = "accessmod_sequence", allocationSize = 1)
public class UserModuleAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accessmod_seq")
    private int accessId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Module module;

    public UserModuleAccess() {
    }

    public UserModuleAccess(int accessId, UserProfile userProfile, Module module) {
        this.accessId = accessId;
        this.userProfile = userProfile;
        this.module = module;
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

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
