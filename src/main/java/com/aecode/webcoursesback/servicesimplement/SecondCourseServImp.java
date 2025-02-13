package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.repositories.ISecondCourseRepo;
import com.aecode.webcoursesback.services.ISecondCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecondCourseServImp implements ISecondCourseService {
    @Autowired
    private ISecondCourseRepo scR;

    @Override
    public void insert(SecondaryCourses secondcourse) {
        scR.save(secondcourse);
    }

    @Override
    public List<SecondaryCourses> list() {
        return scR.findAll();
    }

    @Override
    public void delete(int secondcourseId) {
        scR.deleteById(secondcourseId);
    }

    @Override
    public SecondaryCourses listId(int secondcourseId) {
        return scR.findById(secondcourseId).orElse(new SecondaryCourses());
    }

    @Override
    public SecondaryCourses listByModulexProgram(String moduleNumber, String programTitle) {
        return scR.findByModulexProgram(moduleNumber, programTitle);
    }

    @Override
    public List<SecondaryCourses> paginatedList(int limit, int offset) {
        return scR.paginatedList(limit, offset);
    }
}
