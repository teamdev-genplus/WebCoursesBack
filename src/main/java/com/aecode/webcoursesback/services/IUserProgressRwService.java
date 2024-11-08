package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.UserProgressRW;

import java.util.List;

public interface IUserProgressRwService {

    public void insert(UserProgressRW userprogress);
    List<UserProgressRW> list();
    public void delete(int progressId);
    public UserProgressRW listId(int progressId);

}
