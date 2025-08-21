package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserModuleRepo extends JpaRepository<UserModuleAccess, Long> {

    //NUEVO PARA CLERK
    List<UserModuleAccess> findByUserProfile_ClerkId(String clerkId);

    @Query("SELECT uma.module FROM UserModuleAccess uma WHERE uma.userProfile.clerkId = :clerkId AND uma.module.course.courseId = :courseId")
    List<Module> findModulesByClerkIdAndCourseId(@Param("clerkId") String clerkId, @Param("courseId") Long courseId);

    boolean existsByUserProfile_ClerkIdAndModule_ModuleId(String clerkId, Long moduleId);

    Optional<UserModuleAccess> findByUserProfile_ClerkIdAndModule_ModuleId(String clerkId, Long moduleId);


}
