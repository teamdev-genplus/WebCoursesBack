package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.SessionTest;
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
    public void insert(SessionTest question) {
        qR.save(question);
    }

    @Override
    public List<SessionTest> list() {
        return qR.findAll();
    }

    @Override
    public void delete(int questionId) {
        qR.deleteById(questionId);
    }

    @Override
    public SessionTest listId(int questionId) {
        return qR.findById(questionId).orElse(new SessionTest());
    }
}
