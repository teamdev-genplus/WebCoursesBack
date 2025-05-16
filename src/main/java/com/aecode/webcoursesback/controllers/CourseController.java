package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.servicesimplement.FirebaseStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController  {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    private final ModelMapper modelMapper = new ModelMapper();

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // ------------------- POST -------------------

    // Crear un nuevo curso con datos y subir imagen principal a Firebase
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createCourse(
            @RequestPart("course") String courseJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            CourseDetailDTO courseDTO = objectMapper.readValue(courseJson, CourseDetailDTO.class);
            Course course = modelMapper.map(courseDTO, Course.class);

            // Guardar curso para obtener ID generado
            courseService.insert(course);

            // Subir imagen si existe
            if (image != null && !image.isEmpty()) {
                String safeUrlName = course.getUrlName() != null ? course.getUrlName().replaceAll("[^a-zA-Z0-9_-]", "_") : "course";
                String path = "Course/" + safeUrlName + "_" + course.getCourseId() + "/images/";
                String imageUrl = firebaseStorageService.uploadImage(image, path);
                course.setPrincipalImage(imageUrl);
                courseService.insert(course); // actualizar con url imagen
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Curso creado con ID: " + course.getCourseId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear curso: " + e.getMessage());
        }
    }

    // ------------------- PATCH -------------------

    // Actualizar parcialmente un curso y/o imagen principal
    @PatchMapping(value = "/{courseId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateCourse(
            @PathVariable int courseId,
            @RequestPart(value = "course", required = false) String courseJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Course existingCourse = courseService.getById(courseId);
            if (existingCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado");
            }

            if (courseJson != null && !courseJson.isEmpty()) {
                CourseDetailDTO courseDTO = objectMapper.readValue(courseJson, CourseDetailDTO.class);
                // Actualizar solo campos no nulos
                if (courseDTO.getTitle() != null) existingCourse.setTitle(courseDTO.getTitle());
                if (courseDTO.getDescription() != null) existingCourse.setDescription(courseDTO.getDescription());
                if (courseDTO.getProgramTitle() != null) existingCourse.setProgramTitle(courseDTO.getProgramTitle());
                if (courseDTO.getModule() != null) existingCourse.setModule(courseDTO.getModule());
                if (courseDTO.getBrochureUrl() != null) existingCourse.setBrochureUrl(courseDTO.getBrochureUrl());
                if (courseDTO.getWhatsappGroupLink() != null) existingCourse.setWhatsappGroupLink(courseDTO.getWhatsappGroupLink());
                if (courseDTO.getStartDate() != null) existingCourse.setStartDate(courseDTO.getStartDate());
                if (courseDTO.getCertificateHours() != null) existingCourse.setCertificateHours(courseDTO.getCertificateHours());
                if (courseDTO.getPriceRegular() != null) existingCourse.setPriceRegular(courseDTO.getPriceRegular());
                if (courseDTO.getDiscountPercentage() != null) existingCourse.setDiscountPercentage(courseDTO.getDiscountPercentage());
                if (courseDTO.getPromptPaymentPrice() != null) existingCourse.setPromptPaymentPrice(courseDTO.getPromptPaymentPrice());
                if (courseDTO.getIsOnSale() != null) existingCourse.setIsOnSale(courseDTO.getIsOnSale());
                if (courseDTO.getAchievement() != null) existingCourse.setAchievement(courseDTO.getAchievement());
                if (courseDTO.getOrderNumber() != null) existingCourse.setOrderNumber(courseDTO.getOrderNumber());
                if (courseDTO.getMode() != null) existingCourse.setMode(courseDTO.getMode());
                if (courseDTO.getUrlName() != null) existingCourse.setUrlName(courseDTO.getUrlName());
                if (courseDTO.getUrlMaterialAccess() != null) existingCourse.setUrlMaterialAccess(courseDTO.getUrlMaterialAccess());
                if (courseDTO.getUrlJoinClass() != null) existingCourse.setUrlJoinClass(courseDTO.getUrlJoinClass());
                // Nota: para listas y relaciones complejas, considera actualizar aparte o con lógica específica
            }

            if (image != null && !image.isEmpty()) {
                String path = "Course/" + existingCourse.getUrlName() + "_" + existingCourse.getCourseId() + "/images/";
                String imageUrl = firebaseStorageService.uploadImage(image, path);
                existingCourse.setPrincipalImage(imageUrl);
            }

            courseService.insert(existingCourse);

            return ResponseEntity.ok("Curso actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar curso: " + e.getMessage());
        }
    }

    // ------------------- GET -------------------

    // Listar todos los cursos para vista cards
    @GetMapping("/list")
    public ResponseEntity<List<CourseSummaryDTO>> listAll() {
        List<Course> courses = courseService.listAll();
        List<CourseSummaryDTO> dtos = courses.stream()
                .map(course -> modelMapper.map(course, CourseSummaryDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Listar cursos paginados con offset
    @GetMapping("/paginated")
    public ResponseEntity<Page<CourseSummaryDTO>> paginatedList(
            @RequestParam int offset,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderNumber").ascending());
        Page<CourseSummaryDTO> pageDTO = courseService.paginatedList(offset, pageable);
        return ResponseEntity.ok(pageDTO);
    }

    // Listar cursos paginados filtrando por modo
    @GetMapping("/mode")
    public ResponseEntity<Page<CourseSummaryDTO>> paginateByMode(
            @RequestParam String mode,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderNumber").ascending());
        Page<CourseSummaryDTO> pageDTO = courseService.paginateByMode(mode, pageable);
        return ResponseEntity.ok(pageDTO);
    }

    // Listar cursos filtrados por tags
    @GetMapping("/tags")
    public ResponseEntity<Page<CourseSummaryDTO>> listByTags(
            @RequestParam List<Integer> tagIds,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseSummaryDTO> pageDTO = courseService.listByTags(tagIds, pageable);
        return ResponseEntity.ok(pageDTO);
    }

    // Obtener detalle completo de un curso por id
    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailDTO> getCourseDetail(@PathVariable int id) {
        CourseDetailDTO dto = courseService.getCourseDetail(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // Listar cursos adquiridos por usuario
    @GetMapping("/mycourses/{userId}")
    public ResponseEntity<List<MyCourseDTO>> getMyCourses(@PathVariable int userId) {
        List<Course> courses = courseService.findCoursesByUserId(userId);
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<MyCourseDTO> dtos = courses.stream()
                .map(this::convertToMyCourseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Obtener curso específico adquirido por usuario
    @GetMapping("/mycourses/{userId}/{courseId}")
    public ResponseEntity<MyCourseDTO> getMyCourseById(
            @PathVariable int userId,
            @PathVariable int courseId) {
        MyCourseDTO dto = courseService.getMyCourse(userId, courseId);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(dto);
    }

    // ------------------- DELETE -------------------

    // Eliminar curso por id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable int id) {
        Course course = courseService.getById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.delete(id);
        return ResponseEntity.ok("Curso eliminado correctamente");
    }

    // ------------------- Conversores auxiliares -------------------

    private MyCourseDTO convertToMyCourseDTO(Course course) {
        return MyCourseDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .programTitle(course.getProgramTitle())
                .description(course.getDescription())
                .module(course.getModule())
                .whatsappGroupLink(course.getWhatsappGroupLink())
                .studyPlans(course.getStudyPlans().stream()
                        .map(sp -> StudyPlanDTO.builder()
                                .studyplanId(sp.getStudyplanId())
                                .unit(sp.getUnit())
                                .hours(sp.getHours())
                                .sessions(sp.getSessions())
                                .orderNumber(sp.getOrderNumber())
                                .urlrecording(sp.getUrlrecording())
                                .dmaterial(sp.getDmaterial())
                                .viewpresentation(sp.getViewpresentation())
                                .build())
                        .collect(Collectors.toList()))
                .urlMaterialAccess(course.getUrlMaterialAccess())
                .urlJoinClass(course.getUrlJoinClass())
                .certificates(course.getCertificates().stream()
                        .map(cert -> CertificateDTO.builder()
                                .id(cert.getId())
                                .name(cert.getName())
                                .description(cert.getDescription())
                                .url(cert.getUrl())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
