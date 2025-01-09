package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.entities.UserModuleAccess;

import java.util.List;

public interface IUserModuleService {

    public void insert(UserModuleAccess usermodule);
    List<UserModuleAccess> list();
    public void delete(int accessId);
    public UserModuleAccess listId(int accessId);
}
