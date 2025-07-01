package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUserFavoriteRepo extends JpaRepository<UserFavorite, Long> {

    List<UserFavorite> findByUserProfile_UserId(Long userId);

    Optional<UserFavorite> findByUserProfile_UserIdAndCourse_CourseId(Long userId, Long courseId);

    void deleteByUserProfile_UserIdAndCourse_CourseId(Long userId, Long courseId);

}
