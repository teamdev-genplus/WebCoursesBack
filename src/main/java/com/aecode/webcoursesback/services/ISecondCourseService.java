package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.entities.SecondaryCourses;

import java.util.List;

public interface ISecondCourseService {

    public void insert(SecondaryCourses secondcourse);
    List<SecondaryCourses> list();
    public void delete(int secondcourseId);
    public SecondaryCourses listId(int secondcourseId);
}
