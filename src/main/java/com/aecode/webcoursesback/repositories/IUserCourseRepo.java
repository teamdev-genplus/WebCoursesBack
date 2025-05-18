package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserCourseAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserCourseRepo extends JpaRepository<UserCourseAccess, Integer> {
    List<UserCourseAccess> findByUserProfileUserId(int userId);
    boolean existsByUserProfileUserIdAndCourseCourseId(int userId, int courseId);
}
