package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.UserDetail;
import com.aecode.webcoursesback.repositories.IUserDetailRepo;
import com.aecode.webcoursesback.services.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailServiceImp implements IUserDetailService {
    @Autowired
    private IUserDetailRepo udR;
    @Override
    public void insert(UserDetail userD) {
        udR.save(userD);
    }

    @Override
    public List<UserDetail> list() {
        return udR.findAll();
    }

    @Override
    public void delete(int userD) {
        udR.deleteById(userD);
    }

    @Override
    public UserDetail listId(int userD) {
        return udR.findById(userD).orElse(new UserDetail());
    }
}
