package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.repositories.IClassRepo;
import com.aecode.webcoursesback.services.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImp implements IClassService {
    @Autowired
    private IClassRepo cR;

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
}
