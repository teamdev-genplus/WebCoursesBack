package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.repositories.ISessionRepo;
import com.aecode.webcoursesback.services.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImp implements ISessionService {
    @Autowired
    private ISessionRepo cR;

    @Override
    public void insert(Session classes) {
        cR.save(classes);
    }

    @Override
    public List<Session> list() {
        return cR.findAll();
    }

    @Override
    public void delete(int classId) {
        cR.deleteById(classId);
    }

    @Override
    public Session listId(int classId) {
        return cR.findById(classId).orElse(new Session());
    }

    @Override
    public List<Session> findByTitle(String title) {
        return cR.searchByTitle(title);
    }
}
