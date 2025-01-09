package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.UserModuleAccess;
import com.aecode.webcoursesback.repositories.IUserModuleRepo;
import com.aecode.webcoursesback.services.IUserModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserModuleServiceImp implements IUserModuleService {

    @Autowired
    IUserModuleRepo mR;
    @Override
    public void insert(UserModuleAccess usermodule) {
        mR.save(usermodule);
    }

    @Override
    public List<UserModuleAccess> list() {
        return mR.findAll();
    }

    @Override
    public void delete(int accessId) {
        mR.deleteById(accessId);
    }

    @Override
    public UserModuleAccess listId(int accessId) {
        return mR.findById(accessId).orElse(new UserModuleAccess());
    }
}
