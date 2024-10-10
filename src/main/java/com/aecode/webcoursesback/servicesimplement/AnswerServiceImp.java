package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Answer;
import com.aecode.webcoursesback.repositories.IAnswerRepo;
import com.aecode.webcoursesback.services.IAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImp implements IAnswerService {
    @Autowired
    private IAnswerRepo aR;

    @Override
    public void insert(Answer answer) {
        aR.save(answer);
    }

    @Override
    public List<Answer> list() {
        return aR.findAll();
    }

    @Override
    public void delete(int answerId) {
        aR.deleteById(answerId);
    }

    @Override
    public Answer listId(int answerId) {
        return aR.findById(answerId).orElse(new Answer());
    }
}
