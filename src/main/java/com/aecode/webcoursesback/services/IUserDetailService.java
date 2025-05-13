package com.aecode.webcoursesback.services;
 import com.aecode.webcoursesback.dtos.UserUpdateDTO;
 import com.aecode.webcoursesback.entities.UserDetail;

import java.util.List;

public interface IUserDetailService {
    List<UserDetail> list();
    public UserDetail listId(int userD);
    public UserDetail update(UserDetail userDetail);
    UserDetail findByUserId(int userId);


    public void updateUserDetail(UserUpdateDTO userProfileDTO);

}
