package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserCourseAccess;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserModuleRepo extends JpaRepository<UserModuleAccess, Integer> {
    List<UserModuleAccess> findByUserProfileUserId(int userId);
    boolean existsByUserProfileUserIdAndModuleModuleId(int userId, int courseId);
}
