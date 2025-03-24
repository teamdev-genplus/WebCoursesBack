package com.aecode.webcoursesback.services;

import java.util.List;

import com.aecode.webcoursesback.entities.CourseTag;

public interface ICourseTagService {
    public void insert(CourseTag courseTag);

    List<CourseTag> list();

    public void delete(int courseTagId);

    public CourseTag listById(int courseTagId);
}
