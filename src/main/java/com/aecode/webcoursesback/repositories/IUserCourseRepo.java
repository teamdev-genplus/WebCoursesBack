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
public interface IUserCourseRepo extends JpaRepository<UserCourseAccess, Integer> {
    // Obtener accesos a cursos por usuario
    List<UserCourseAccess> findByUserProfile_UserId(Long userId);

    // Verificar si usuario tiene acceso completo a un curso
    boolean existsByUserProfile_UserIdAndCourse_CourseId(Long userId, Long courseId);

    @Query("SELECT new com.aecode.webcoursesback.dtos.UserCourseDTO(uca.accessId, uca.userProfile.userId, uca.course.courseId, uca.completed) FROM UserCourseAccess uca")
    List<UserCourseDTO> findAllUserCourseDTOs();
}
