package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.entities.UserProgress;

import java.util.List;

public interface IUserProgressService {

    public void insert(UserProgress userprogress);
    List<UserProgress> list();
    public void delete(int progressId);
    public UserProgress listId(int progressId);

}
