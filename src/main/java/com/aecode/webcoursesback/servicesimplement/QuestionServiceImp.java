package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Question;
import com.aecode.webcoursesback.repositories.IQuestionRepo;
import com.aecode.webcoursesback.services.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImp implements IQuestionService {
    @Autowired
    private IQuestionRepo qR;

    @Override
    public void insert(Question question) {
        qR.save(question);
    }

    @Override
    public List<Question> list() {
        return qR.findAll();
    }

    @Override
    public void delete(int questionId) {
        qR.deleteById(questionId);
    }

    @Override
    public Question listId(int questionId) {
        return qR.findById(questionId).orElse(new Question());
    }
}
