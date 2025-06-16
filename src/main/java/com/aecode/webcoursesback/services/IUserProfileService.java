package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.LoginDTO;
import com.aecode.webcoursesback.dtos.RegistrationDTO;
import com.aecode.webcoursesback.dtos.UserProfileDTO;
import com.aecode.webcoursesback.dtos.UserUpdateDTO;
import com.aecode.webcoursesback.entities.UserProfile;

import java.util.List;

public interface IUserProfileService {
    public void insert(RegistrationDTO dto);
    List<UserProfile> list();
    public void delete(Long userId);
    public UserProfile listId(Long userId);
    public void update(UserProfile userprofile);
    public UserProfile authenticateUser(LoginDTO logindto);

    void changePassword(Long userId, String currentPassword, String newPassword);


    public List<UserUpdateDTO> listusers();
    public UserUpdateDTO listusersId(Long userId);


}
