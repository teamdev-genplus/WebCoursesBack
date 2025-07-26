package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.IUserAccessService;
import jakarta.transaction.Transactional;
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
    private IUserProfileRepository userProfileRepo;

    @Autowired
    private ICourseRepo courseRepo;

    @Autowired
    private IModuleRepo moduleRepo;

    @Autowired
    private ModelMapper modelMapper;

    private UserProfile getUserByClerkId(String clerkId) {
        return userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con Clerk ID no encontrado: " + clerkId));
    }

    @Override
    public UserCourseAccess grantCourseAccess(String clerkId, Long courseId) {
        UserProfile user = getUserByClerkId(clerkId);
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));

        UserCourseAccess access = UserCourseAccess.builder()
                .userProfile(user)
                .course(course)
                .completed(false)
                .build();
        userCourseAccessRepo.save(access);

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

        userModuleAccessRepo.saveAll(moduleAccessList);
        return access;
    }

    @Override
    public UserModuleAccess grantModuleAccess(String clerkId, Long moduleId) {
        UserProfile user = getUserByClerkId(clerkId);
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
    public List<CourseCardProgressDTO> getAccessibleCoursesForUser(String clerkId) {
        List<UserCourseAccess> fullAccess = userCourseAccessRepo.findByUserProfile_ClerkId(clerkId);

        List<CourseCardProgressDTO> fullAccessCards = fullAccess.stream().map(uca -> {
            Course course = uca.getCourse();
            return CourseCardProgressDTO.builder()
                    .courseId(course.getCourseId())
                    .title(course.getTitle())
                    .principalImage(course.getPrincipalImage())
                    .orderNumber(course.getOrderNumber())
                    .urlnamecourse(course.getUrlnamecourse())
                    .completed(uca.isCompleted())
                    .build();
        }).collect(Collectors.toList());

        List<UserModuleAccess> partialAccess = userModuleAccessRepo.findByUserProfile_ClerkId(clerkId);

        Set<Long> alreadyIncluded = fullAccess.stream()
                .map(uca -> uca.getCourse().getCourseId())
                .collect(Collectors.toSet());

        Map<Long, List<UserModuleAccess>> groupedByCourse = partialAccess.stream()
                .filter(uma -> !alreadyIncluded.contains(uma.getModule().getCourse().getCourseId()))
                .collect(Collectors.groupingBy(uma -> uma.getModule().getCourse().getCourseId()));

        List<CourseCardProgressDTO> partialAccessCards = new ArrayList<>();

        for (Map.Entry<Long, List<UserModuleAccess>> entry : groupedByCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<UserModuleAccess> moduleAccesses = entry.getValue();

            Optional<Course> courseOpt = courseRepo.findById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
                boolean isCompleted = allModules.size() == moduleAccesses.stream().filter(UserModuleAccess::isCompleted).count();

                partialAccessCards.add(CourseCardProgressDTO.builder()
                        .courseId(course.getCourseId())
                        .title(course.getTitle())
                        .principalImage(course.getPrincipalImage())
                        .orderNumber(course.getOrderNumber())
                        .urlnamecourse(course.getUrlnamecourse())
                        .completed(isCompleted)
                        .build());
            }
        }

        List<CourseCardProgressDTO> result = new ArrayList<>();
        result.addAll(fullAccessCards);
        result.addAll(partialAccessCards);
        return result;
    }

    @Override
    public boolean hasAccessToModule(String clerkId, Long moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));
        Long courseId = module.getCourse().getCourseId();

        boolean hasFullAccess = userCourseAccessRepo.existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
        if (hasFullAccess) return true;

        return userModuleAccessRepo.existsByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);
    }

    @Override
    public ModuleDTO getFirstAccessibleModuleForUser(String clerkId, Long courseId) {
        boolean hasFullAccess = userCourseAccessRepo.existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
        List<Module> accessibleModules;

        if (hasFullAccess) {
            accessibleModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        } else {
            accessibleModules = userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                    .stream()
                    .sorted(Comparator.comparing(Module::getOrderNumber))
                    .collect(Collectors.toList());
        }

        if (accessibleModules.isEmpty()) return null;

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
                .orElseThrow(() -> new EntityNotFoundException("No tienes acceso a este Módulo"));
        return modelMapper.map(module, ModuleDTO.class);
    }

    @Override
    @Transactional
    public boolean markModuleAsCompleted(String clerkId, Long moduleId) {
        Optional<UserModuleAccess> accessOpt = userModuleAccessRepo.findByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);
        if (accessOpt.isPresent()) {
            UserModuleAccess access = accessOpt.get();
            access.setCompleted(true);
            userModuleAccessRepo.save(access);
            return true;
        }
        return false;
    }

    @Override
    public List<UserModuleAccess> grantMultipleModuleAccess(String clerkId, List<Long> moduleIds) {
        UserProfile user = getUserByClerkId(clerkId);
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
    public List<UserModuleDTO> getUserModulesByClerkId(String clerkId) {
        List<UserModuleAccess> accesses = userModuleAccessRepo.findByUserProfile_ClerkId(clerkId);
        return accesses.stream()
                .map(access -> UserModuleDTO.builder()
                        .accessId(access.getAccessId())
                        .clerkId(access.getUserProfile().getClerkId())
                        .moduleId(access.getModule().getModuleId())
                        .completed(access.isCompleted())
                        .build())
                .collect(Collectors.toList());
    }
}
