package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Class;

import java.util.List;

public interface IClassService {
    public void insert(Class classes);
    List<Class> list();
    public void delete(int classId);
    public Class listId(int classId);
}
