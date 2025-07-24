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

        // Guardar acceso al curso
        UserCourseAccess access = UserCourseAccess.builder()
                .userProfile(user)
                .course(course)
                .completed(false)
                .build();
        userCourseAccessRepo.save(access);

        // Obtener todos los módulos del curso y registrar acceso
        List<Module> modules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        List<UserModuleAccess> moduleAccessList = new ArrayList<>();

        for (Module module : modules) {
            UserModuleAccess moduleAccess = UserModuleAccess.builder()
                    .userProfile(user)
                    .module(module)
                    .completed(false)
                    .build();
            moduleAccessList.add(moduleAccess);
        }

        userModuleAccessRepo.saveAll(moduleAccessList); // Guardar todos de una sola vez

        return access;
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
        // Obtener accesos completos
        List<UserCourseAccess> fullAccess = userCourseAccessRepo.findByUserProfile_UserId(userId);

        // Mapear cursos con acceso completo
        List<CourseCardDTO> fullAccessCards = fullAccess.stream().map(uca -> {
            Course course = uca.getCourse();
            return CourseCardDTO.builder()
                    .courseId(course.getCourseId())
                    .title(course.getTitle())
                    .principalImage(course.getPrincipalImage())
                    .orderNumber(course.getOrderNumber())
                    .type(course.getType())
                    .cantModOrHours(course.getCantModOrHours())
                    .mode(course.getMode())
                    .urlnamecourse(course.getUrlnamecourse())
                    .completed(uca.isCompleted()) // ✅ Marca si el curso fue completado por el usuario
                    .build();
        }).collect(Collectors.toList());

        // Obtener accesos parciales (módulos sueltos)
        List<UserModuleAccess> partialAccess = userModuleAccessRepo.findByUserProfile_UserId(userId);

        // Obtener los cursos que ya se incluyeron arriba (para evitar duplicados)
        Set<Long> alreadyIncluded = fullAccess.stream()
                .map(uca -> uca.getCourse().getCourseId())
                .collect(Collectors.toSet());

        // Mapear accesos parciales
        Map<Long, List<UserModuleAccess>> groupedByCourse = partialAccess.stream()
                .filter(uma -> !alreadyIncluded.contains(uma.getModule().getCourse().getCourseId())) // evitar duplicados
                .collect(Collectors.groupingBy(uma -> uma.getModule().getCourse().getCourseId()));

        List<CourseCardDTO> partialAccessCards = new ArrayList<>();

        for (Map.Entry<Long, List<UserModuleAccess>> entry : groupedByCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<UserModuleAccess> moduleAccesses = entry.getValue();

            Optional<Course> courseOpt = courseRepo.findById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
                boolean isCompleted = allModules.size() == moduleAccesses.stream().filter(UserModuleAccess::isCompleted).count();

                partialAccessCards.add(CourseCardDTO.builder()
                        .courseId(course.getCourseId())
                        .title(course.getTitle())
                        .principalImage(course.getPrincipalImage())
                        .orderNumber(course.getOrderNumber())
                        .type(course.getType())
                        .cantModOrHours(course.getCantModOrHours())
                        .mode(course.getMode())
                        .urlnamecourse(course.getUrlnamecourse())
                        .completed(isCompleted) // ✅ Completado solo si terminó todos los módulos comprados
                        .build());
            }
        }

        // Combinar ambos y devolver
        List<CourseCardDTO> result = new ArrayList<>();
        result.addAll(fullAccessCards);
        result.addAll(partialAccessCards);
        return result;
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

    @Override
    public boolean markModuleAsCompleted(Long userId, Long moduleId) {
        Optional<UserModuleAccess> accessOpt = userModuleAccessRepo.findByUserProfile_UserIdAndModule_ModuleId(userId, moduleId);
        if (accessOpt.isPresent()) {
            UserModuleAccess access = accessOpt.get();
            access.setCompleted(true);
            userModuleAccessRepo.save(access);
            return true;
        }
        return false;
    }

    @Override
    public List<UserModuleAccess> grantMultipleModuleAccess(Long userId, List<Long> moduleIds) {
        UserProfile user = new UserProfile();
        user.setUserId(userId);

        List<Module> modules = moduleRepo.findAllById(moduleIds);
        List<UserModuleAccess> accessList = new ArrayList<>();

        for (Module module : modules) {
            UserModuleAccess access = UserModuleAccess.builder()
                    .userProfile(user)
                    .module(module)
                    .completed(false)
                    .build();
            accessList.add(access);
        }

        return userModuleAccessRepo.saveAll(accessList);
    }

    @Override
    public List<UserModuleDTO> getUserModulesByUserId(Long userId) {
        List<UserModuleAccess> accesses = userModuleAccessRepo.findByUserProfile_UserId(userId);
        return accesses.stream()
                .map(access -> UserModuleDTO.builder()
                        .accessId(access.getAccessId())
                        .userId(access.getUserProfile().getUserId())
                        .moduleId(access.getModule().getModuleId())
                        .build())
                .toList();
    }


}
