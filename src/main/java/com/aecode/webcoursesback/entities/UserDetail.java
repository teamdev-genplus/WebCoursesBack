package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "userdetail")
@SequenceGenerator(name = "userdetail_seq", sequenceName = "userdetail_sequence", allocationSize = 1)

public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userdetail_seq")
    private int detailsId;

    @OneToOne(fetch = FetchType.EAGER) // Cambiar a EAGER para cargar siempre el UserProfile
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserProfile userProfile;

    @Column(length = 255)
    private String profilepicture;

    public UserDetail() {
    }

    public UserDetail(int detailsId, UserProfile userProfile, String profilepicture) {
        this.detailsId = detailsId;
        this.userProfile = userProfile;
        this.profilepicture = profilepicture;
    }

    public int getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(int detailsId) {
        this.detailsId = detailsId;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }
}
