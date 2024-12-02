package com.aecode.webcoursesback.dtos;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class UserDetailDTO {
    private int detailsId;
    private int userId;
    private String profilepicture;

    public int getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(int detailsId) {
        this.detailsId = detailsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

}
