package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.UserProgressRW;
import com.aecode.webcoursesback.repositories.IUserProgressRwRepo;
import com.aecode.webcoursesback.services.IUserProgressRwService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProgressRwServiceImp implements IUserProgressRwService {

    @Autowired
    private IUserProgressRwRepo upuR;
    @Override
    public void insert(UserProgressRW userprogress) {
        upuR.save(userprogress);
    }

    @Override
    public List<UserProgressRW> list() {
        return upuR.findAll();
    }

    @Override
    public void delete(int progressId) {
        upuR.deleteById(progressId);
    }

    @Override
    public UserProgressRW listId(int progressId) {
        return upuR.findById(progressId).orElse(new UserProgressRW());
    }
}
