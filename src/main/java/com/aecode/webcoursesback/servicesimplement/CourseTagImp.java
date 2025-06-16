package com.aecode.webcoursesback.servicesimplement;

import java.util.List;

import com.aecode.webcoursesback.entities.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aecode.webcoursesback.repositories.ICourseTagRepo;
import com.aecode.webcoursesback.services.ICourseTagService;

@Service
public class CourseTagImp implements ICourseTagService {
    @Autowired
    private ICourseTagRepo courseTagRepo;

    @Override
    public void insert(Tag tag) {
        courseTagRepo.save(tag);
    }

    @Override
    public List<Tag> list() {
        return courseTagRepo.findAll();
    }

    @Override
    public Tag listById(int tagId) {
        return courseTagRepo.findById(tagId).get();
    }

    @Override
    public void delete(int tagId) {
        courseTagRepo.deleteById(tagId);
    }
}
