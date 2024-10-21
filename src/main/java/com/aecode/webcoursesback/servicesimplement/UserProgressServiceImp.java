package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.UserProgressSession;
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
    public void insert(UserProgressSession userprogress) {
        upR.save(userprogress);
    }

    @Override
    public List<UserProgressSession> list() {
        return upR.findAll();
    }

    @Override
    public void delete(int progressId) {
        upR.deleteById(progressId);
    }

    @Override
    public UserProgressSession listId(int progressId) {
        return upR.findById(progressId).orElse(new UserProgressSession());
    }
}
