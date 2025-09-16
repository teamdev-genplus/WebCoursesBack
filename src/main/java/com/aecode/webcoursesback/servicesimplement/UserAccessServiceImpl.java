package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.CourseUnitDTO;
import com.aecode.webcoursesback.dtos.Profile.ModuleAccessDTO;
import com.aecode.webcoursesback.dtos.Profile.ModuleProfileDTO;
import com.aecode.webcoursesback.dtos.Profile.ToolLinkDTO;
import com.aecode.webcoursesback.dtos.VClassroom.ModuleContentDTO;
import com.aecode.webcoursesback.dtos.VClassroom.VideoCardDTO;
import com.aecode.webcoursesback.dtos.VClassroom.VideoCompletionDTO;
import com.aecode.webcoursesback.dtos.VClassroom.VideoPlayDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.VClassroom.ModuleVideo;
import com.aecode.webcoursesback.entities.VClassroom.UserVideoCompletion;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.repositories.VClassroom.ModuleVideoRepository;
import com.aecode.webcoursesback.repositories.VClassroom.UserVideoCompletionRepository;
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
    @Autowired private IShoppingCartRepo shoppingCartRepo;
    // inyecciones nuevas:
    @Autowired private ModuleResourceLinkRepository moduleResourceLinkRepo;
    @Autowired private ModuleVideoRepository moduleVideoRepository;
    @Autowired private UserVideoCompletionRepository userVideoCompletionRepository;

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
                    .urlnamecourse(c.getUrlnamecourse())
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
                            .urlnamecourse(course.getUrlnamecourse())
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
        boolean hasFullAccess = userCourseAccessRepo
                .existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);

        // Determina el "primer" módulo accesible según acceso total o parcial
        List<Module> accessibleModules = hasFullAccess
                ? moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId)
                : userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                .stream()
                .sorted(Comparator.comparing(Module::getOrderNumber))
                .toList();

        if (accessibleModules.isEmpty()) return null;

        Module firstModule = accessibleModules.get(0);
        Course course = firstModule.getCourse();

        // Lista completa (para tabs)
        List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        boolean showTabs = allModules.size() > 1;

        // IDs con acceso (cuando es parcial)
        Set<Long> userModuleIds = userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                .stream().map(Module::getModuleId).collect(Collectors.toSet());

        // Tabs (solo si hay >1 módulo en el curso)
        List<ModuleAccessDTO> moduleAccessList = showTabs
                ? allModules.stream().map(m ->
                ModuleAccessDTO.builder()
                        .moduleId(m.getModuleId())
                        .courseId(courseId)
                        .programTitle(m.getProgramTitle())
                        .orderNumber(m.getOrderNumber())
                        .hasAccess(hasFullAccess || userModuleIds.contains(m.getModuleId()))
                        .build()
        ).toList()
                : List.of();

        // Etiqueta por tipo de curso
        String courseTypeLabel = null;
        if ("modular".equalsIgnoreCase(course.getType())) {
            courseTypeLabel = "Programa especializado";
        } else if ("diplomado".equalsIgnoreCase(course.getType())) {
            courseTypeLabel = "Diplomado Internacional";
        }

        // Título preferente
        String titleStudyplan = (firstModule.getTitleStudyplan() != null && !firstModule.getTitleStudyplan().isBlank())
                ? firstModule.getTitleStudyplan()
                : firstModule.getProgramTitle();

        // Modo / dinámicas de horario
        String mode = (firstModule.getMode() != null) ? firstModule.getMode().name() : null;
        boolean isLive = isLiveOrExclusiveMode(firstModule);  // ENVIVO o EXCLUSIVO => true
        boolean available247 = !isLive;

        List<ScheduleDTO> schedulesDto = resolveSchedulesFor(firstModule);
        String urlJoinClass = isLive ? firstModule.getUrlJoinClass() : null;

        // Herramientas desplegables
        List<ToolLinkDTO> toolLinks  = moduleResourceLinkRepo
                .findActiveByModuleIdOrderByOrderNumberAsc(firstModule.getModuleId())
                .stream()
                .map(l -> ToolLinkDTO.builder().name(l.getName()).url(l.getUrl()).build())
                .toList();

        // NUEVO: toolPictures (solo si no es EXCLUSIVO)
        List<String> toolPictures = resolveToolPictures(firstModule);

        // Certificados (solo el nombre)
        List<MyCertificateDTO> certs = mapCertificates(firstModule.getCertificates());

        // WhatsApp (del módulo)
        String whatsapp = resolveWhatsapp(course, firstModule);

        // Study Plan
        List<StudyPlanDTO> studyPlans = mapStudyPlans(firstModule.getStudyPlans());

        return ModuleProfileDTO.builder()
                .moduleId(firstModule.getModuleId())
                .courseId(courseId)
                .titleStudyplan(titleStudyplan)
                .courseTypeLabel(courseTypeLabel)

                // NUEVOS botones de módulo
                .urlrecording(firstModule.getUrlrecording())
                .viewpresentation(firstModule.getViewpresentation())

                .studyPlans(studyPlans)
                .orderNumber(firstModule.getOrderNumber())

                .whatsappGroupLink(whatsapp)
                .tools(toolLinks)            // dropdown
                .toolPictures(toolPictures)  // solo imágenes de Tool (si no es EXCLUSIVO)

                .mode(mode)
                .isLive(isLive)
                .available247(available247)
                .schedules(schedulesDto)
                .urlJoinClass(urlJoinClass)

                .certificates(certs)
                .courseModules(moduleAccessList)

                .urlimagelogo1(firstModule.getUrlimagelogo1())
                .urlimagelogo2(firstModule.getUrlimagelogo2())
                .build();
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
        Course course = module.getCourse();

        boolean hasFullAccess = userCourseAccessRepo
                .existsByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
        boolean hasAccess = hasFullAccess || userModuleAccessRepo
                .existsByUserProfile_ClerkIdAndModule_ModuleId(clerkId, moduleId);

        if (!hasAccess) {
            throw new EntityNotFoundException("No tienes acceso a este Módulo");
        }

        // Lista completa (para tabs)
        List<Module> allModules = moduleRepo.findByCourse_CourseIdOrderByOrderNumberAsc(courseId);
        boolean showTabs = allModules.size() > 1;

        // IDs con acceso (cuando es parcial)
        Set<Long> userModuleIds = userModuleAccessRepo.findModulesByClerkIdAndCourseId(clerkId, courseId)
                .stream().map(Module::getModuleId).collect(Collectors.toSet());

        List<ModuleAccessDTO> moduleAccessList = showTabs
                ? allModules.stream().map(m ->
                ModuleAccessDTO.builder()
                        .moduleId(m.getModuleId())
                        .courseId(courseId)
                        .programTitle(m.getProgramTitle())
                        .orderNumber(m.getOrderNumber())
                        .hasAccess(hasFullAccess || userModuleIds.contains(m.getModuleId()))
                        .build()
        ).toList()
                : List.of();

        // Etiqueta por tipo de curso
        String courseTypeLabel = null;
        if ("modular".equalsIgnoreCase(course.getType())) {
            courseTypeLabel = "Programa especializado";
        } else if ("diplomado".equalsIgnoreCase(course.getType())) {
            courseTypeLabel = "Diplomado Internacional";
        }

        // Título preferente
        String titleStudyplan = (module.getTitleStudyplan() != null && !module.getTitleStudyplan().isBlank())
                ? module.getTitleStudyplan()
                : module.getProgramTitle();

        // Modo / dinámicas de horario
        String mode = (module.getMode() != null) ? module.getMode().name() : null;
        boolean isLive = isLiveOrExclusiveMode(module);  // ENVIVO o EXCLUSIVO => true
        boolean available247 = !isLive;

        // 👇 helper para horarios o “Disponible 24/7”
        List<ScheduleDTO> schedulesDto = resolveSchedulesFor(module);
        String urlJoinClass = isLive ? module.getUrlJoinClass() : null;

        // Herramientas desplegables
        List<ToolLinkDTO> toolLinks  = moduleResourceLinkRepo
                .findActiveByModuleIdOrderByOrderNumberAsc(module.getModuleId())
                .stream()
                .map(l -> ToolLinkDTO.builder().name(l.getName()).url(l.getUrl()).build())
                .toList();

        // NUEVO: toolPictures (solo si no es EXCLUSIVO)
        List<String> toolPictures = resolveToolPictures(module);

        // Certificados (solo nombre)
        List<MyCertificateDTO> certs = mapCertificates(module.getCertificates());

        // WhatsApp (del módulo)
        String whatsapp = resolveWhatsapp(course, module);

        // Study Plan
        List<StudyPlanDTO> studyPlans = mapStudyPlans(module.getStudyPlans());

        return ModuleProfileDTO.builder()
                .moduleId(module.getModuleId())
                .courseId(courseId)
                .titleStudyplan(titleStudyplan)
                .courseTypeLabel(courseTypeLabel)

                // NUEVOS botones de módulo
                .urlrecording(module.getUrlrecording())
                .viewpresentation(module.getViewpresentation())

                .studyPlans(studyPlans)
                .orderNumber(module.getOrderNumber())

                .whatsappGroupLink(whatsapp)
                .tools(toolLinks)            // dropdown
                .toolPictures(toolPictures)  // solo imágenes de Tool (si no es EXCLUSIVO)

                .mode(mode)
                .isLive(isLive)
                .available247(available247)
                .schedules(schedulesDto)
                .urlJoinClass(urlJoinClass)

                .certificates(certs)
                .courseModules(moduleAccessList)

                .urlimagelogo1(module.getUrlimagelogo1())
                .urlimagelogo2(module.getUrlimagelogo2())
                .build();
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

    @Override
    public ModuleContentDTO getModuleContent(String clerkId, Long moduleId, Long videoIdOrNull) {
        // 1) Validar acceso
        if (!hasAccessToModule(clerkId, moduleId)) {
            throw new EntityNotFoundException("No tienes acceso a este Módulo");
        }

        // 2) Traer módulo + videos
        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        Long courseId = module.getCourse() != null ? module.getCourse().getCourseId() : null;
        var videos = moduleVideoRepository.findByModule_ModuleIdOrderByOrderNumberAsc(moduleId);

        // 3) Progreso (completed) del usuario para este módulo
        var completionMap = userVideoCompletionRepository
                .findByUserProfile_ClerkIdAndVideo_Module_ModuleId(clerkId, moduleId)
                .stream().collect(Collectors.toMap(c -> c.getVideo().getId(), c -> c.isCompleted()));

        // 4) Playlist DTO
        var playlist = videos.stream().map(v -> VideoCardDTO.builder()
                .videoId(v.getId())
                .sessionTitle(v.getSessionTitle())
                .sessionLabel(v.getSessionLabel())
                .orderNumber(v.getOrderNumber())
                .thumbnailUrl(v.getThumbnailUrl())
                .completed(completionMap.getOrDefault(v.getId(), false))
                .build()
        ).toList();

        // 5) Elegir video actual (por param o el primero)
        ModuleVideo current = null;
        if (videoIdOrNull != null) {
            Long id = videoIdOrNull;
            current = videos.stream().filter(v -> v.getId().equals(id)).findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("El video no pertenece a este módulo"));
        } else if (!videos.isEmpty()) {
            current = videos.get(0);
        }

        // 6) Construir headerTitle: "Clase {orden} de {total} - {label}"
        VideoPlayDTO currentDTO = null;
        if (current != null) {
            int total = videos.size();
            String base = (current.getSessionLabel() != null && !current.getSessionLabel().isBlank())
                    ? current.getSessionLabel()
                    : current.getSessionTitle();
            String headerTitle = String.format("Clase %d de %d - %s",
                    current.getOrderNumber(), total, base);

            currentDTO = VideoPlayDTO.builder()
                    .videoId(current.getId())
                    .headerTitle(headerTitle)
                    .sessionTitle(current.getSessionTitle())
                    .sessionLabel(current.getSessionLabel())
                    .orderNumber(current.getOrderNumber())
                    .videoUrl(current.getVideoUrl())
                    .description(current.getDescription())
                    .materialUrl(current.getMaterialUrl())
                    .completed(completionMap.getOrDefault(current.getId(), false))
                    .build();
        }

        return ModuleContentDTO.builder()
                .moduleId(moduleId)
                .courseId(courseId)
                .moduleTitle(module.getTitleStudyplan())
                .totalVideos(videos.size())
                .current(currentDTO)
                .playlist(playlist)
                .build();
    }

    @Override
    @Transactional
    public VideoCompletionDTO markVideoCompleted(String clerkId, Long videoId, Boolean completedOrNull) {
        var video = moduleVideoRepository.findById(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Video no encontrado"));

        // validar acceso por módulo
        if (!hasAccessToModule(clerkId, video.getModule().getModuleId())) {
            throw new EntityNotFoundException("No tienes acceso a este Módulo");
        }

        var user = userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        var completion = userVideoCompletionRepository
                .findByUserProfile_ClerkIdAndVideo_Id(clerkId, videoId)
                .orElseGet(() -> UserVideoCompletion.builder()
                        .userProfile(user)
                        .video(video)
                        .completed(false)
                        .build());

        boolean newValue = (completedOrNull == null) ? true : completedOrNull;
        completion.setCompleted(newValue);

        userVideoCompletionRepository.save(completion);

        return VideoCompletionDTO.builder()
                .videoId(videoId)
                .clerkId(clerkId)
                .completed(newValue)
                .build();
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
    private String resolveWhatsapp(Course course, Module module) {
        // Siempre usamos el enlace del módulo (heredado de BaseProduct)
        return module.getWhatsappGroupLink();
    }

    // Mapea 1:1 desde entidad a DTO; mantiene scheduleName como string libre
    private List<ScheduleDTO> mapSchedules(List<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) return List.of();
        return schedules.stream()
                .map(s -> ScheduleDTO.builder()
                        .scheduleId(s.getScheduleId())
                        .scheduleName(s.getScheduleName())       // si está set, el front lo usa tal cual
                        .startDateTime(s.getStartDateTime())      // opcional
                        .endDateTime(s.getEndDateTime())          // opcional
                        .timezone(s.getTimezone())                // opcional
                        .build())
                .toList();
    }

    private List<MyCertificateDTO> mapCertificates(List<Certificate> certificates) {
        if (certificates == null || certificates.isEmpty()) return List.of();
        return certificates.stream().map(c ->
                MyCertificateDTO.builder()
                        .certificateName(c.getName())   // solo nombre en esta vista
                        .certificateImage(null)
                        .certificateUrl(null)
                        .moduleName(null)
                        .achieved(false)
                        .build()
        ).toList();
    }

    private List<StudyPlanDTO> mapStudyPlans(List<StudyPlan> plans) {
        if (plans == null || plans.isEmpty()) return List.of();
        return plans.stream()
                .map(p -> modelMapper.map(p, StudyPlanDTO.class))
                .toList();
    }

    /**
     * Si el módulo es ENVIVO o EXCLUSIVO => mapea sus schedules reales.
     * Si NO es ninguno => devuelve un único ScheduleDTO con "Disponible 24/7".
     */
    private List<ScheduleDTO> resolveSchedulesFor(Module module) {
        if (isLiveOrExclusiveMode(module)) {
            return mapSchedules(module.getSchedules()); // usa tu mapper 1:1 ya implementado
        }
        // Mensaje calculado (no se persiste en BD)
        return List.of(
                ScheduleDTO.builder()
                        .scheduleId(null)
                        .scheduleName("Disponible 24/7")
                        .startDateTime(null)
                        .endDateTime(null)
                        .timezone(null)
                        .build()
        );
    }

    /**
     * Devuelve la lista de URLs de los logos de herramientas (Tool.picture)
     * SOLO si el módulo NO es EXCLUSIVO. En EXCLUSIVO => lista vacía.
     */
    private List<String> resolveToolPictures(Module module) {
        boolean isExclusive = module.getMode() != null && "EXCLUSIVO".equalsIgnoreCase(module.getMode().name());
        if (isExclusive) return List.of();

        List<Tool> tools = module.getTools();
        if (tools == null || tools.isEmpty()) return List.of();

        return tools.stream()
                .map(Tool::getPicture)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    /** Modo que debe mostrar horarios reales y botón de clase. */
    private boolean isLiveOrExclusiveMode(Module module) {
        return module.getMode() != null &&
                ("ENVIVO".equalsIgnoreCase(module.getMode().name())
                        || "EXCLUSIVO".equalsIgnoreCase(module.getMode().name()));
    }




}
