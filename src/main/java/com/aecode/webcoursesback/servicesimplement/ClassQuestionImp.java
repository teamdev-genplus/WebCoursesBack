package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.ClassQuestion;
import com.aecode.webcoursesback.repositories.IClassQuestionRepo;
import com.aecode.webcoursesback.repositories.IClassRepo;
import com.aecode.webcoursesback.services.IClassQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassQuestionImp implements IClassQuestionService {
    @Autowired
    private IClassQuestionRepo qR;
    @Autowired
    private IClassRepo cR;
    @Override
    public void insert(ClassQuestion question) {
        qR.save(question);
    }

    @Override
    public List<ClassQuestion> list() {
        return qR.findAll();
    }

    @Override
    public void delete(int questionId) {
        qR.deleteById(questionId);
    }

    @Override
    public ClassQuestion listId(int questionId) {
        return qR.findById(questionId).orElse(new ClassQuestion());
    }
}
