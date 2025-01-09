package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.SecondaryCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISecondCourseRepo extends JpaRepository<SecondaryCourses,Integer> {
}
