package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.ClassAnswer;
import com.aecode.webcoursesback.repositories.IClassAnswerRepo;
import com.aecode.webcoursesback.services.IClassAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassAnswerImp  implements IClassAnswerService {
    @Autowired
    private IClassAnswerRepo aR;
    @Override
    public void insert(ClassAnswer answer) {
        aR.save(answer);
    }

    @Override
    public List<ClassAnswer> list() {
        return aR.findAll();
    }

    @Override
    public void delete(int answerId) {
        aR.deleteById(answerId);
    }

    @Override
    public ClassAnswer listId(int answerId) {
        return aR.findById(answerId).orElse(new ClassAnswer());
    }
}
