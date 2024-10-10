package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Test;
import com.aecode.webcoursesback.repositories.ITestRepo;
import com.aecode.webcoursesback.services.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImp implements ITestService {
    @Autowired
    private ITestRepo tR;

    @Override
    public void insert(Test test) {
        tR.save(test);
    }

    @Override
    public List<Test> list() {
        return tR.findAll();
    }

    @Override
    public void delete(int testId) {
        tR.deleteById(testId);
    }

    @Override
    public Test listId(int testId) {
        return tR.findById(testId).orElse(new Test());
    }
}
