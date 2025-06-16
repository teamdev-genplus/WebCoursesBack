package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.dtos.UserCourseDTO;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.entities.UserCourseAccess;
import com.aecode.webcoursesback.entities.UserModuleAccess;

import java.util.List;

public interface IUserAccessService {
    // Registrar acceso a curso completo
    UserCourseAccess grantCourseAccess(Long userId, Long courseId);

    // Registrar acceso a módulo individual
    UserModuleAccess grantModuleAccess(Long userId, Long moduleId);

    // Obtener lista de cursos accesibles (completo o parcial) para mostrar cards en "Mis Cursos"
    List<CourseCardDTO> getAccessibleCoursesForUser(Long userId);

    // Validar acceso a módulo
    boolean hasAccessToModule(Long userId, Long moduleId);

    //obtener el primer módulo comprado de un curso por usuario
    ModuleDTO getFirstAccessibleModuleForUser(Long userId, Long courseId);

    //Listar todo para curso
    List<UserCourseDTO> getAllCourses();

    //Listar todo para modulo
    List<UserModuleDTO> getAllModules();

    public ModuleDTO getModuleById(Long moduleId);
}
