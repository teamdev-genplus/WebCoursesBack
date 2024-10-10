package com.aecode.webcoursesback.services;


import com.aecode.webcoursesback.entities.ClassQuestion;

import java.util.List;

public interface IClassQuestionService {
    public void insert(ClassQuestion question);
    List<ClassQuestion> list();
    public void delete(int questionId);
    public ClassQuestion listId(int questionId);
}
