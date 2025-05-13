package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.entities.UserSecCourseAccess;
import com.aecode.webcoursesback.repositories.IUserSecCourseRepo;
import com.aecode.webcoursesback.services.IUserSecCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSecCourseServiceImp implements IUserSecCourseService {
    @Autowired
    IUserSecCourseRepo uscR;

    @Override
    public void insert(UserSecCourseAccess userseccourse) {
        uscR.save(userseccourse);
    }

    @Override
    public List<UserSecCourseAccess> list() {
        return uscR.findAll();
    }

    @Override
    public void delete(int accessId) {
        uscR.deleteById(accessId);
    }

    @Override
    public UserSecCourseAccess listId(int accessId) {
        return uscR.findById(accessId).orElse(new UserSecCourseAccess());
    }
}
