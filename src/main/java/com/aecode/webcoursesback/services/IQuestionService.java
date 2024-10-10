package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Question;

import java.util.List;

public interface IQuestionService {
    public void insert(Question question);
    List<Question> list();
    public void delete(int questionId);
    public Question listId(int questionId);
}
