package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.CourseUnitDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private IShoppingCartRepo shoppingCartRepo;


    private UserProfile getUserByClerkId(String clerkId) {
        return userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con Clerk ID no encontrado: " + clerkId));
    }

    // COMPRA: Dar acceso total a un curso y todos sus módulos
    @Override
    public UserCourseDTO grantCourseAccess(String clerkId, Long courseId) {
        UserProfile user = userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with clerkId: " + clerkId));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        UserCourseAccess access = UserCourseAccess.builder()
                .userProfile(user)
                .course(course)
                .completed(false)
                .build();

        UserCourseAccess savedAccess = userCourseAccessRepo.save(access);

        return UserCourseDTO.builder()
                .accessId((long) savedAccess.getAccessId())
                .clerkId(clerkId)
                .courseId(courseId)
                .completed(savedAccess.isCompleted())
                .build();
    }

    // COMPRA: Dar acceso individual a un módulo
    @Override
    public UserModuleDTO  grantModuleAccess(String clerkId, Long moduleId) {
        UserProfile user = userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with clerkId: " + clerkId));

        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with ID: " + moduleId));

        UserModuleAccess access = UserModuleAccess.builder()
                .userProfile(user)
                .module(module)
                .completed(false)
                .build();

        UserModuleAccess savedAccess = userModuleAccessRepo.save(access);

        return UserModuleDTO.builder()
                .accessId((long) savedAccess.getAccessId())
                .clerkId(clerkId)
                .moduleId(moduleId)
                .completed(savedAccess.isCompleted())
                .build();
    }

    // PERFIL: Obtener los cursos accesibles desde "Mis cursos"
    @Override
    public List<CourseCardProgressDTO> getAccessibleCoursesForUser(String clerkId) {
        // 0) Traer TODAS las UMAs del usuario de una vez
        List<UserModuleAccess> allUserUMAs = userModuleAccessRepo.findByUserProfile_ClerkId(clerkId);

        // Agrupar UMAs por courseId (solo las que están ligadas a un curso)
        Map<Long, List<UserModuleAccess>> umaByCourseId = allUserUMAs.stream()
                .filter(uma -> uma.getModule() != null && uma.getModule().getCourse() != null)
                .collect(Collectors.groupingBy(uma -> uma.getModule().getCourse().getCourseId()));

        // 1) cursos completos (full access)
        List<UserCourseAccess> fullAccess = userCourseAccessRepo.findByUserProfile_ClerkId(clerkId);
        Set<Long> fullCourseIds = fullAccess.stream()
                .map(a -> a.getCourse().getCourseId())
                .collect(Collectors.toSet());

        List<CourseCardProgressDTO> fullAccessCards = fullAccess.stream().map(uca -> {
            Course c = uca.getCourse();
            List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(c.getCourseId());

            // usar UMAs de este curso para marcar completados
            List<UserModuleAccess> userUMAsForThisCourse = umaByCourseId.getOrDefault(c.getCourseId(), Collections.emptyList());

            List<CourseUnitDTO> units = buildUnitsForCourse(c, allModules, userUMAsForThisCourse, true);

            return CourseCardProgressDTO.builder()
                    .courseId(c.getCourseId())
                    .title(c.getTitle())
                    .principalImage(c.getPrincipalImage())
                    .orderNumber(c.getOrderNumber())
                    .type(c.getType())
                    .units(units) // todas con hasAccess=true, y completed según UMAs
                    .build();
        }).toList();

        // 2) acceso parcial (por módulos): cursos donde NO tiene acceso total
        // Filtramos entradas del mapa umaByCourseId excluyendo cursos con full access
        List<CourseCardProgressDTO> partialAccessCards = umaByCourseId.entrySet().stream()
                .filter(entry -> !fullCourseIds.contains(entry.getKey()))
                .map(entry -> {
                    Long courseId = entry.getKey();
                    Course course = courseRepo.findById(courseId).orElse(null);
                    if (course == null) return null;

                    List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
                    List<UserModuleAccess> userUMAsForThisCourse = entry.getValue();

                    // hasFullAccess=false => marcar hasAccess solo para módulos adquiridos
                    List<CourseUnitDTO> units = buildUnitsForCourse(course, allModules, userUMAsForThisCourse, false);

                    return CourseCardProgressDTO.builder()
                            .courseId(course.getCourseId())
                            .title(course.getTitle())
                            .principalImage(course.getPrincipalImage())
                            .orderNumber(course.getOrderNumber())
                            .type(course.getType())
                            .units(units)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        // 3) combinar en un solo resultado
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

        List<Module> modules = hasFullAccess
                ? moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId)
                : userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                .stream().sorted(Comparator.comparing(Module::getOrderNumber)).toList();

        if (modules.isEmpty()) return null;

        Module firstModule = modules.get(0);
        ModuleProfileDTO dto = modelMapper.map(firstModule, ModuleProfileDTO.class);

        //calcular acceso módulo por módulo
        List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        Set<Long> userModuleIds = userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                .stream().map(Module::getModuleId).collect(Collectors.toSet());

        List<ModuleAccessDTO> moduleAccessList = allModules.stream().map(m ->
                ModuleAccessDTO.builder()
                        .moduleId(m.getModuleId())
                        .courseId(courseId)
                        .programTitle(m.getProgramTitle())
                        .orderNumber(m.getOrderNumber())
                        .hasAccess(hasFullAccess || userModuleIds.contains(m.getModuleId()))
                        .build()
        ).toList();

        dto.setCourseModules(moduleAccessList);
        return dto;
    }
    @Override
    public ModuleProfileDTO getFirstAccessibleModuleForUserBySlug(String clerkId, String urlnamecourse) {
        Course course = courseRepo.findByUrlnamecourse(urlnamecourse)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with urlnamecourse: " + urlnamecourse));

        return getFirstAccessibleModuleForUser(clerkId, course.getCourseId());
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
    public List<UserModuleDTO> grantMultipleModuleAccess(String clerkId, List<Long> moduleIds) {
        UserProfile user = getUserByClerkId(clerkId);

        List<Module> modules = moduleRepo.findAllById(moduleIds);

        List<UserModuleAccess> accessList = modules.stream().map(m ->
                UserModuleAccess.builder()
                        .userProfile(user)
                        .module(m)
                        .completed(false)
                        .build()
        ).toList();

        List<UserModuleAccess> saved = userModuleAccessRepo.saveAll(accessList);

        // 4. BORRAR del carrito los módulos comprados
        shoppingCartRepo.deleteByUserProfile_ClerkIdAndModule_ModuleIdIn(clerkId, moduleIds);

        return saved.stream().map(access -> UserModuleDTO.builder()
                .accessId((long) access.getAccessId())
                .clerkId(clerkId)
                .moduleId(access.getModule().getModuleId())
                .completed(access.isCompleted())
                .build()).toList();
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Construye la lista de unidades visibles en el card (módulos o paquetes).
     * @param course        El curso
     * @param allModules    Todos los módulos asociados al curso
     * @param userModulesForThisCourse     Los módulos que el usuario tiene en UserModuleAccess
     * @param hasFullAccess true si el usuario compró el curso completo
     */
    private List<CourseUnitDTO> buildUnitsForCourse(
            Course course,
            List<Module> allModules,
            List<UserModuleAccess> userModulesForThisCourse,
            boolean hasFullAccess
    ) {
        // Mapa rápido: moduleId -> completed (de lo que el usuario tiene en UserModuleAccess)
        Map<Long, Boolean> completedByModuleId = userModulesForThisCourse.stream()
                .filter(uma -> uma.getModule() != null && uma.getModule().getModuleId() != null)
                .collect(Collectors.toMap(
                        uma -> uma.getModule().getModuleId(),
                        UserModuleAccess::isCompleted,
                        (a, b) -> a || b // si por algún motivo hay duplicados, si uno es true queda true
                ));

        // Conjunto de módulos que el usuario posee (acceso) por compra parcial
        Set<Long> ownedModuleIds = new HashSet<>(completedByModuleId.keySet());

        List<CourseUnitDTO> units = new ArrayList<>();

        for (int i = 0; i < allModules.size(); i++) {
            Module m = allModules.get(i);
            Long moduleId = m.getModuleId();

            // Nombre visible por tipo
            String displayName;
            if ("modular".equalsIgnoreCase(course.getType())) {
                displayName = "Módulo " + (i + 1);
            } else if ("diplomado".equalsIgnoreCase(course.getType())) {
                displayName = (allModules.size() == 1) ? "Paquete Completo" : "Paquete " + (i + 1);
            } else {
                displayName = (m.getProgramTitle() != null && !m.getProgramTitle().isBlank())
                        ? m.getProgramTitle()
                        : "Unidad " + (i + 1);
            }

            boolean hasAccess;
            boolean completed;

            if (hasFullAccess) {
                // Curso completo: todas las unidades tienen acceso
                hasAccess = true;
                // Completado solo si aparece UMA con completed=true
                completed = completedByModuleId.getOrDefault(moduleId, false);
            } else {
                // Acceso parcial: acceso solo si hay UMA para ese módulo
                hasAccess = ownedModuleIds.contains(moduleId);
                completed = hasAccess && completedByModuleId.getOrDefault(moduleId, false);
            }

            units.add(CourseUnitDTO.builder()
                    .moduleId(moduleId)
                    .displayName(displayName)
                    .hasAccess(hasAccess)  // 👈 NUEVO
                    .completed(completed)
                    .build());
        }

        return units;
    }

}
