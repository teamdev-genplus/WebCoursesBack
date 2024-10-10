package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.entities.ClassAnswer;
import java.util.List;

public interface IClassAnswerService {
    public void insert(ClassAnswer answer);
    List<ClassAnswer> list();
    public void delete(int answerId);
    public ClassAnswer listId(int answerId);
}
