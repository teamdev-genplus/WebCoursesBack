package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Session;

import java.util.List;

public interface IClassService {
    public void insert(Session classes);
    List<Session> list();
    public void delete(int classId);
    public Session listId(int classId);
}
