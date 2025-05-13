package com.aecode.webcoursesback.services;


import com.aecode.webcoursesback.entities.UserSecCourseAccess;

import java.util.List;

public interface IUserSecCourseService {
    public void insert(UserSecCourseAccess userseccourse);
    List<UserSecCourseAccess> list();
    public void delete(int accessId);
    public UserSecCourseAccess listId(int accessId);
}
