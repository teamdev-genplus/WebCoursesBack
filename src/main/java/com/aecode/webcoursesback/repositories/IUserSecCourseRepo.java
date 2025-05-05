package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserSecCourseAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserSecCourseRepo extends JpaRepository<UserSecCourseAccess, Integer> {
}
