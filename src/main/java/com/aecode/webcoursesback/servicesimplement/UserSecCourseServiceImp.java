package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.entities.UserSecCourseAccess;
import com.aecode.webcoursesback.repositories.IUserSecCourseRepo;
import com.aecode.webcoursesback.services.IUserSecCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void markCompleted(int accessId, boolean completed) {
        Optional<UserSecCourseAccess> opt = uscR.findById(accessId);
        if (opt.isPresent()) {
            UserSecCourseAccess access = opt.get();
            access.setCompleted(completed);
            uscR.save(access);
        } else {
            throw new RuntimeException("Acceso al curso no encontrado con id: " + accessId);
        }
    }

    @Override
    public boolean existsByUserProfileUserIdAndSeccourseSeccourseId(Long userId, Long courseId) {
        return uscR.existsByUserProfileUserIdAndSeccourseSeccourseId(userId, courseId);
    }
}
