package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.dtos.UserCourseDTO;
import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.IUserAccessService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAccessServiceImpl implements IUserAccessService {

    @Autowired
    private IUserCourseRepo userCourseAccessRepo;

    @Autowired
    private IUserModuleRepo userModuleAccessRepo;

    @Autowired
    private ICourseRepo courseRepo;

    @Autowired
    private IModuleRepo moduleRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserCourseAccess grantCourseAccess(Long userId, Long courseId) {
        UserProfile user = new UserProfile();
        user.setUserId(userId);
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));

        UserCourseAccess access = UserCourseAccess.builder()
                .userProfile(user)
                .course(course)
                .completed(false)
                .build();

        return userCourseAccessRepo.save(access);
    }

    @Override
    public UserModuleAccess grantModuleAccess(Long userId, Long moduleId) {
        UserProfile user = new UserProfile();
        user.setUserId(userId);
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        UserModuleAccess access = UserModuleAccess.builder()
                .userProfile(user)
                .module(module)
                .completed(false)
                .build();

        return userModuleAccessRepo.save(access);
    }

    @Override
    public List<CourseCardDTO> getAccessibleCoursesForUser(Long userId) {
        // Cursos con acceso completo
        List<UserCourseAccess> fullAccess = userCourseAccessRepo.findByUserProfile_UserId(userId);
        Set<Long> courseIds = fullAccess.stream()
                .map(uca -> uca.getCourse().getCourseId())
                .collect(Collectors.toSet());

        // Cursos con acceso parcial (a partir de módulos)
        List<UserModuleAccess> partialAccess = userModuleAccessRepo.findByUserProfile_UserId(userId);
        partialAccess.stream()
                .map(uma -> uma.getModule().getCourse().getCourseId())
                .forEach(courseIds::add);

        // Obtener cursos únicos
        List<Course> courses = courseRepo.findAllById(courseIds);

        // Mapear a DTOs
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseCardDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAccessToModule(Long userId, Long moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));
        Long courseId = module.getCourse().getCourseId();

        boolean hasFullAccess = userCourseAccessRepo.existsByUserProfile_UserIdAndCourse_CourseId(userId, courseId);
        if (hasFullAccess) return true;

        return userModuleAccessRepo.existsByUserProfile_UserIdAndModule_ModuleId(userId, moduleId);
    }

    @Override
    public ModuleDTO getFirstAccessibleModuleForUser(Long userId, Long courseId) {
        boolean hasFullAccess = userCourseAccessRepo.existsByUserProfile_UserIdAndCourse_CourseId(userId, courseId);
        List<Module> accessibleModules;

        if (hasFullAccess) {
            // Usuario tiene acceso completo, obtener todos los módulos ordenados
            accessibleModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        } else {
            // Usuario tiene acceso parcial, obtener solo módulos comprados ordenados
            accessibleModules = userModuleAccessRepo.findModulesByUserIdAndCourseId(userId, courseId)
                    .stream()
                    .sorted(Comparator.comparing(Module::getOrderNumber))
                    .collect(Collectors.toList());
        }

        if (accessibleModules.isEmpty()) {
            return null; // No tiene acceso a ningún módulo
        }

        // Mapear el primer módulo a DTO y devolver
        return modelMapper.map(accessibleModules.get(0), ModuleDTO.class);
    }

    @Override
    public List<UserCourseDTO> getAllCourses() {
        return userCourseAccessRepo.findAllUserCourseDTOs();
    }

    @Override
    public List<UserModuleDTO> getAllModules() {
        return userModuleAccessRepo.findAllUserModuleDTOs();
    }

    @Override
    public ModuleDTO getModuleById(Long moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("No tienes acceso a este Modulo"));
        return modelMapper.map(module, ModuleDTO.class);
    }
}
