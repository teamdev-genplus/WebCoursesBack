package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Answer;

import java.util.List;

public interface IAnswerService {
    public void insert(Answer answer);
    List<Answer> list();
    public void delete(int answerId);
    public Answer listId(int answerId);
}
