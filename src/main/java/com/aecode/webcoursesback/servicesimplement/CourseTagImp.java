package com.aecode.webcoursesback.servicesimplement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aecode.webcoursesback.entities.CourseTag;
import com.aecode.webcoursesback.repositories.ICourseTagRepo;
import com.aecode.webcoursesback.services.ICourseTagService;

@Service
public class CourseTagImp implements ICourseTagService {
    @Autowired
    private ICourseTagRepo courseTagRepo;

    @Override
    public void insert(CourseTag courseTag) {
        courseTagRepo.save(courseTag);
    }

    @Override
    public List<CourseTag> list() {
        return courseTagRepo.findAll();
    }

    @Override
    public CourseTag listById(int courseTagId) {
        return courseTagRepo.findById(courseTagId).get();
    }

    @Override
    public void delete(int courseTagId) {
        courseTagRepo.deleteById(courseTagId);
    }
}
