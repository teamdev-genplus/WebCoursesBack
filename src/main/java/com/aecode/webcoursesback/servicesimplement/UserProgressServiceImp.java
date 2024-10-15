package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.UserProgress;
import com.aecode.webcoursesback.repositories.IUserProgressRepo;
import com.aecode.webcoursesback.services.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProgressServiceImp implements IUserProgressService {

    @Autowired
    private IUserProgressRepo upR;
    @Override
    public void insert(UserProgress userprogress) {
        upR.save(userprogress);
    }

    @Override
    public List<UserProgress> list() {
        return upR.findAll();
    }

    @Override
    public void delete(int progressId) {
        upR.deleteById(progressId);
    }

    @Override
    public UserProgress listId(int progressId) {
        return upR.findById(progressId).orElse(new UserProgress());
    }
}
