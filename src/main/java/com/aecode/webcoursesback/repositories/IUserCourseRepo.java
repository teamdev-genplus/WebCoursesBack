package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.dtos.UserCourseDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.UserCourseAccess;
import com.aecode.webcoursesback.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserCourseRepo extends JpaRepository<UserCourseAccess, Long> {

    @Query("SELECT new com.aecode.webcoursesback.dtos.UserCourseDTO(uca.accessId, uca.userProfile.clerkId, uca.course.courseId, uca.completed) FROM UserCourseAccess uca")
    List<UserCourseDTO> findAllUserCourseDTOs();

    //NUEVO PARA CLERK
    List<UserCourseAccess> findByUserProfile_ClerkId(String clerkId);

    boolean existsByUserProfile_ClerkIdAndCourse_CourseId(String clerkId, Long courseId);


}
