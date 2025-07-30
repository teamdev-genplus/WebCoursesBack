package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserFavoriteRepo extends JpaRepository<UserFavorite, Long> {

    List<UserFavorite> findByUserProfile_ClerkId(String clerkId);

    Optional<UserFavorite> findByUserProfile_ClerkIdAndCourse_CourseId(String clerkId, Long courseId);

    void deleteByUserProfile_ClerkIdAndCourse_CourseId(String clerkId, Long courseId);

}
