package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.SecondCourseSummaryDTO;
import com.aecode.webcoursesback.entities.SecondaryCourses;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISecondCourseService {

    public void insert(SecondaryCourses secondcourse);

    List<SecondaryCourses> list();

    public void delete(Long secondcourseId);

    public SecondaryCourses listId(Long secondcourseId);

    public SecondaryCourses listByModulexProgram(String moduleNumber, String programTitle);

    public Page<SecondCourseSummaryDTO> paginatedList(int offsetCourseId, Pageable pageable);

    public Page<SecondCourseSummaryDTO> paginateByMode(String mode, Pageable pageable);
}
