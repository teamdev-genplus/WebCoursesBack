package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Test;

import java.util.List;

public interface ITestService {
    public void insert(Test test);
    List<Test> list();
    public void delete(int testId);
    public Test listId(int testId);
}
