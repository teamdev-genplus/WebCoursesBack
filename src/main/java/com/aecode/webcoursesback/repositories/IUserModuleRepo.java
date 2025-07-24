package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserModuleRepo extends JpaRepository<UserModuleAccess, Integer> {
    // Obtener accesos a módulos por usuario
    List<UserModuleAccess> findByUserProfile_UserId(Long userId);

    // Obtener módulos accesibles de un curso para un usuario
    @Query("SELECT uma.module FROM UserModuleAccess uma WHERE uma.userProfile.userId = :userId AND uma.module.course.courseId = :courseId")
    List<Module> findModulesByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    // Verificar si usuario tiene acceso a un módulo específico
    boolean existsByUserProfile_UserIdAndModule_ModuleId(Long userId, Long moduleId);

    @Query("SELECT new com.aecode.webcoursesback.dtos.UserModuleDTO(uma.accessId, uma.userProfile.userId, uma.module.moduleId) FROM UserModuleAccess uma")
    List<UserModuleDTO> findAllUserModuleDTOs();
}
