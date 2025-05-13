package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserSecCourseAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserSecCourseRepo extends JpaRepository<UserSecCourseAccess, Integer> {

    List<UserSecCourseAccess> findByUserProfileUserId(int userId);

    boolean existsByUserProfileUserIdAndSeccourseSeccourseId(int userId, Long courseId);
}
