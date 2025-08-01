package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.ModuleAccessDTO;
import com.aecode.webcoursesback.dtos.Profile.ModuleProfileDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.IUserAccessService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAccessServiceImpl implements IUserAccessService {

    @Autowired private IUserCourseRepo userCourseAccessRepo;
    @Autowired private IUserModuleRepo userModuleAccessRepo;
    @Autowired private IUserProfileRepository userProfileRepo;
    @Autowired private ICourseRepo courseRepo;
    @Autowired private IModuleRepo moduleRepo;
    @Autowired private ModelMapper modelMapper;

    private UserProfile getUserByClerkId(String clerkId) {
        return userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con Clerk ID no encontrado: " + clerkId));
    }

    // COMPRA: Dar acceso total a un curso y todos sus módulos
    @Override
    public UserCourseAccess grantCourseAccess(String clerkId, Long courseId) {
        UserProfile user = getUserByClerkId(clerkId);
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));

        UserCourseAccess access = userCourseAccessRepo.save(
                UserCourseAccess.builder().userProfile(user).course(course).completed(false).build()
        );

        List<Module> modules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        List<UserModuleAccess> moduleAccessList = modules.stream().map(module ->
                UserModuleAccess.builder().userProfile(user).module(module).completed(false).build()
        ).collect(Collectors.toList());

        userModuleAccessRepo.saveAll(moduleAccessList);
        return access;
    }

    // COMPRA: Dar acceso individual a un módulo
    @Override
    public UserModuleAccess grantModuleAccess(String clerkId, Long moduleId) {
        UserProfile user = getUserByClerkId(clerkId);
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        return userModuleAccessRepo.save(
                UserModuleAccess.builder().userProfile(user).module(module).completed(false).build()
        );
    }

    // PERFIL: Obtener los cursos accesibles desde "Mis cursos"
    @Override
    public List<CourseCardProgressDTO> getAccessibleCoursesForUser(String clerkId) {
        List<UserCourseAccess> fullAccess = userCourseAccessRepo.findByUserProfile_ClerkId(clerkId);
        Set<Long> fullCourseIds = fullAccess.stream().map(a -> a.getCourse().getCourseId()).collect(Collectors.toSet());

        List<CourseCardProgressDTO> fullAccessCards = fullAccess.stream().map(uca -> {
            Course c = uca.getCourse();
            return CourseCardProgressDTO.builder()
                    .courseId(c.getCourseId())
                    .title(c.getTitle())
                    .principalImage(c.getPrincipalImage())
                    .orderNumber(c.getOrderNumber())
                    .urlnamecourse(c.getUrlnamecourse())
                    .completed(uca.isCompleted())
                    .build();
        }).toList();

        List<UserModuleAccess> partialAccess = userModuleAccessRepo.findByUserProfile_ClerkId(clerkId);
        Map<Long, List<UserModuleAccess>> grouped = partialAccess.stream()
                .filter(uma -> !fullCourseIds.contains(uma.getModule().getCourse().getCourseId()))
                .collect(Collectors.groupingBy(uma -> uma.getModule().getCourse().getCourseId()));

        List<CourseCardProgressDTO> partialAccessCards = grouped.entrySet().stream().map(entry -> {
            Course course = courseRepo.findById(entry.getKey()).orElse(null);
            if (course == null) return null;
            List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(entry.getKey());
            boolean completed = entry.getValue().stream().filter(UserModuleAccess::isCompleted).count() == allModules.size();
            return CourseCardProgressDTO.builder()
                    .courseId(course.getCourseId())
                    .title(course.getTitle())
                    .principalImage(course.getPrincipalImage())
                    .orderNumber(course.getOrderNumber())
                    .urlnamecourse(course.getUrlnamecourse())
                    .completed(completed)
                    .build();
        }).filter(Objects::nonNull).toList();

        List<CourseCardProgressDTO> result = new ArrayList<>(fullAccessCards);
        result.addAll(partialAccessCards);
        return result;
    }

    // VALIDACIÓN: ¿El usuario tiene acceso al módulo?
    @Override
    public boolean hasAccessToModule(String clerkId, Long moduleId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));
        Long courseId = module.getCourse().getCourseId();

        return userCourseAccessRepo.existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId)
                || userModuleAccessRepo.existsByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);
    }

    // ACCESO: Obtener el primer módulo accesible (cuando el usuario entra desde el card del curso)
    @Override
    public ModuleProfileDTO getFirstAccessibleModuleForUser(String clerkId, Long courseId) {
        boolean hasFullAccess = userCourseAccessRepo.existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
        List<Module> modules = hasFullAccess ?
                moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId) :
                userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                        .stream().sorted(Comparator.comparing(Module::getOrderNumber)).toList();

        return modules.isEmpty() ? null : modelMapper.map(modules.get(0), ModuleProfileDTO.class);
    }

    // ACCESO: Obtener un módulo específico si tiene acceso
    @Override
    public ModuleProfileDTO getModuleById(Long moduleId, String clerkId) {
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        Long courseId = module.getCourse().getCourseId();
        boolean hasFullAccess = userCourseAccessRepo.existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
        boolean hasAccess = hasFullAccess || userModuleAccessRepo.existsByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);

        if (!hasAccess) throw new EntityNotFoundException("No tienes acceso a este Módulo");

        ModuleProfileDTO dto = modelMapper.map(module, ModuleProfileDTO.class);

        List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        Set<Long> userModuleIds = userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                .stream().map(Module::getModuleId).collect(Collectors.toSet());

        List<ModuleAccessDTO> moduleaccess = allModules.stream().map(m ->
                ModuleAccessDTO.builder()
                        .moduleId(m.getModuleId())
                        .courseId(courseId)
                        .programTitle(m.getProgramTitle())
                        .orderNumber(m.getOrderNumber())
                        .hasAccess(hasFullAccess || userModuleIds.contains(m.getModuleId()))
                        .build()
        ).toList();

        dto.setCourseModules(moduleaccess);
        return dto;
    }

    // TRACKING: Marcar un módulo como completado
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

    // COMPRA MASIVA: Dar acceso a múltiples módulos
    @Override
    public List<UserModuleAccess> grantMultipleModuleAccess(String clerkId, List<Long> moduleIds) {
        UserProfile user = getUserByClerkId(clerkId);
        List<Module> modules = moduleRepo.findAllById(moduleIds);
        List<UserModuleAccess> accessList = modules.stream().map(m ->
                UserModuleAccess.builder().userProfile(user).module(m).completed(false).build()
        ).collect(Collectors.toList());

        return userModuleAccessRepo.saveAll(accessList);
    }

    // ADMIN/USER: Obtener módulos con acceso por usuario
    @Override
    public List<UserModuleDTO> getUserModulesByClerkId(String clerkId) {
        return userModuleAccessRepo.findByUserProfile_ClerkId(clerkId).stream().map(a ->
                UserModuleDTO.builder()
                        .accessId(a.getAccessId())
                        .clerkId(a.getUserProfile().getClerkId())
                        .moduleId(a.getModule().getModuleId())
                        .completed(a.isCompleted())
                        .build()
        ).collect(Collectors.toList());
    }

    // ADMIN: Obtener todos los cursos con acceso
    @Override
    public List<UserCourseDTO> getAllCourses() {
        return userCourseAccessRepo.findAllUserCourseDTOs();
    }

    // ADMIN: Obtener todos los módulos con acceso
    @Override
    public List<UserModuleDTO> getAllModules() {
        return userModuleAccessRepo.findAllUserModuleDTOs();
    }
}
