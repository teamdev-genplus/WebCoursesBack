package com.aecode.webcoursesback.dtos;

public class UserSecCourseDTO {
    private int accessId;
    private int userId;
    private Long seccourseId;

    public int getAccessId() {
        return accessId;
    }

    public void setAccessId(int accessId) {
        this.accessId = accessId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Long getSeccourseId() {
        return seccourseId;
    }

    public void setSeccourseId(Long seccourseId) {
        this.seccourseId = seccourseId;
    }
}
