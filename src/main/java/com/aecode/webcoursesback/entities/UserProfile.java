package com.aecode.webcoursesback.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userprofiles")
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)

public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private int userId;

    @Column(length = 50)
    private String fullname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthdate;

    @Column( length = 255)
    private String phoneNumber;

    @Column(length = 100)
    private String gender;

    @Column(length = 100)
    private String experience;

    @Column(length = 100)
    private String rol = "user";

    @Column(length = 100)
    private String status = "Activo";

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressSession> userprogresssessions = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressRW> userprogressrw = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCourseAccess> usercourseaccess = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserModuleAccess> usermoduleaccess = new ArrayList<>();

    public UserProfile() {
    }

    public UserProfile(int userId, String fullname, String email, String passwordHash, LocalDate birthdate, String phoneNumber, String gender, String experience, String rol, String status, List<UserProgressSession> userprogresssessions, List<UserProgressRW> userprogressrw, List<UserCourseAccess> usercourseaccess, List<UserModuleAccess> usermoduleaccess) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.experience = experience;
        this.rol = rol;
        this.status = status;
        this.userprogresssessions = userprogresssessions;
        this.userprogressrw = userprogressrw;
        this.usercourseaccess = usercourseaccess;
        this.usermoduleaccess = usermoduleaccess;
    }

    public List<UserModuleAccess> getUsermoduleaccess() {
        return usermoduleaccess;
    }

    public void setUsermoduleaccess(List<UserModuleAccess> usermoduleaccess) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<UserProgressSession> getUserprogresssessions() {
        return userprogresssessions;
    }

    public void setUserprogresssessions(List<UserProgressSession> userprogresssessions) {
        this.userprogresssessions = userprogresssessions;
    }

    public List<UserProgressRW> getUserprogressrw() {
        return userprogressrw;
    }

    public void setUserprogressrw(List<UserProgressRW> userprogressrw) {
        this.userprogressrw = userprogressrw;
    }

    public List<UserCourseAccess> getUsercourseaccess() {
        return usercourseaccess;
    }

    public void setUsercourseaccess(List<UserCourseAccess> usercourseaccess) {
        this.usercourseaccess = usercourseaccess;
    }

}
