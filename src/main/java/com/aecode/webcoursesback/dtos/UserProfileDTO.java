package com.aecode.webcoursesback.dtos;

import com.aecode.webcoursesback.entities.UserModuleAccess;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.util.List;

public class UserProfileDTO {
    private int userId;
    private String fullname;
    private String email;
    private String passwordHash;
    private String rol;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private LocalDate birthdate;
    private String phoneNumber;
    private String gender;
    private String experience;

    private List<UserProgressSessionDTO> userprogresssessions;
    private List<UserProgressRwDTO> userprogressrw;
    private List<UserCourseDTO> usercourseaccess;
    private List<UserModuleDTO> usermoduleaccess;

    public List<UserModuleDTO> getUsermoduleaccess() {
        return usermoduleaccess;
    }

    public void setUsermoduleaccess(List<UserModuleDTO> usermoduleaccess) {
        this.usermoduleaccess = usermoduleaccess;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public List<UserProgressSessionDTO> getUserprogresssessions() {
        return userprogresssessions;
    }

    public void setUserprogresssessions(List<UserProgressSessionDTO> userprogresssessions) {
        this.userprogresssessions = userprogresssessions;
    }

    public List<UserProgressRwDTO> getUserprogressrw() {
        return userprogressrw;
    }

    public void setUserprogressrw(List<UserProgressRwDTO> userprogressrw) {
        this.userprogressrw = userprogressrw;
    }

    public List<UserCourseDTO> getUsercourseaccess() {
        return usercourseaccess;
    }

    public void setUsercourseaccess(List<UserCourseDTO> usercourseaccess) {
        this.usercourseaccess = usercourseaccess;
    }
}
