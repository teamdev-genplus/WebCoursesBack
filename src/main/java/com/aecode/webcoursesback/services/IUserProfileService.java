package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.MyProfileDTO;
import com.aecode.webcoursesback.entities.UserProfile;

import java.util.List;

public interface IUserProfileService {
    public void insert(RegistrationDTO dto);

    public void insertuserClerk(UserClerkDTO dto);
    List<UserProfile> list();
    public void delete(Long userId);
    public UserProfile listId(Long userId);
    public void update(UserProfile userprofile);
    public UserProfile authenticateUser(LoginDTO logindto);

    void changePassword(Long userId, String currentPassword, String newPassword);


    public List<UserUpdateDTO> listusers();
    public UserUpdateDTO listClerkId(String clerkId);

    MyProfileDTO getMyProfile(String clerkId);


}
