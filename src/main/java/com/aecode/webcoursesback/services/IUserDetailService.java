package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.entities.UserDetail;

import java.util.List;

public interface IUserDetailService {
    public void insert(UserDetail userD);
    List<UserDetail> list();
    public void delete(int userD);
    public UserDetail listId(int userD);
}
