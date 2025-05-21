package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Module.Mode;
import com.aecode.webcoursesback.repositories.IUserModuleRepo;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.servicesimplement.FirebaseStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/modules")
public class ModuleController {
    @Autowired
    private IModuleService mS;

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    private final ModelMapper modelMapper = new ModelMapper();

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // ------------------- POST -------------------

    /**
     * Crear un nuevo módulo con datos y subir imagen principal a Firebase
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createModule(
            @RequestPart("module") String moduleJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ModuleDetailDTO moduleDTO = objectMapper.readValue(moduleJson, ModuleDetailDTO.class);
            Module module = modelMapper.map(moduleDTO, Module.class);

            // Guardar módulo para obtener ID generado
            moduleService.insert(module);

            // Subir imagen si existe
            if (image != null && !image.isEmpty()) {
                String safeUrlName = module.getUrlName() != null ? module.getUrlName().replaceAll("[^a-zA-Z0-9_-]", "_") : "module";
                String path = "Module/" + safeUrlName + "_" + module.getModuleId() + "/images/";
                String imageUrl = firebaseStorageService.uploadImage(image, path);
                module.setPrincipalImage(imageUrl);
                moduleService.insert(module); // actualizar con url imagen
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Módulo creado con ID: " + module.getModuleId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear módulo: " + e.getMessage());
        }
    }

    // ------------------- PATCH -------------------

    /**
     * Actualizar parcialmente un módulo y/o imagen principal
     */
    @PatchMapping(value = "/{moduleId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateModule(
            @PathVariable int moduleId,
            @RequestPart(value = "module", required = false) String moduleJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Module existingModule = moduleService.listId(moduleId);
            if (existingModule == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo no encontrado");
            }

            if (moduleJson != null && !moduleJson.isEmpty()) {
                ModuleDetailDTO moduleDTO = objectMapper.readValue(moduleJson, ModuleDetailDTO.class);

                // Actualizar solo campos no nulos
                if (moduleDTO.getProgramTitle() != null) existingModule.setProgramTitle(moduleDTO.getProgramTitle());
                if (moduleDTO.getDescription() != null) existingModule.setDescription(moduleDTO.getDescription());
                if (moduleDTO.getBrochureUrl() != null) existingModule.setBrochureUrl(moduleDTO.getBrochureUrl());
                if (moduleDTO.getWhatsappGroupLink() != null) existingModule.setWhatsappGroupLink(moduleDTO.getWhatsappGroupLink());
                if (moduleDTO.getStartDate() != null) existingModule.setStartDate(moduleDTO.getStartDate());
                if (moduleDTO.getCertificateHours() != null) existingModule.setCertificateHours(moduleDTO.getCertificateHours());
                if (moduleDTO.getPriceRegular() != null) existingModule.setPriceRegular(moduleDTO.getPriceRegular());
                if (moduleDTO.getDiscountPercentage() != null) existingModule.setDiscountPercentage(moduleDTO.getDiscountPercentage());
                if (moduleDTO.getPromptPaymentPrice() != null) existingModule.setPromptPaymentPrice(moduleDTO.getPromptPaymentPrice());
                if (moduleDTO.getIsOnSale() != null) existingModule.setIsOnSale(moduleDTO.getIsOnSale());
                if (moduleDTO.getAchievement() != null) existingModule.setAchievement(moduleDTO.getAchievement());
                if (moduleDTO.getOrderNumber() != null) existingModule.setOrderNumber(moduleDTO.getOrderNumber());
                if (moduleDTO.getMode() != null) existingModule.setMode(moduleDTO.getMode());
                if (moduleDTO.getUrlName() != null) existingModule.setUrlName(moduleDTO.getUrlName());
                if (moduleDTO.getUrlMaterialAccess() != null) existingModule.setUrlMaterialAccess(moduleDTO.getUrlMaterialAccess());
                if (moduleDTO.getUrlJoinClass() != null) existingModule.setUrlJoinClass(moduleDTO.getUrlJoinClass());
                // Para listas y relaciones complejas, considera actualizar aparte o con lógica específica
            }

            if (image != null && !image.isEmpty()) {
                String path = "Module/" + existingModule.getUrlName() + "_" + existingModule.getModuleId() + "/images/";
                String imageUrl = firebaseStorageService.uploadImage(image, path);
                existingModule.setPrincipalImage(imageUrl);
            }

            moduleService.insert(existingModule);

            return ResponseEntity.ok("Módulo actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar módulo: " + e.getMessage());
        }
    }

    // ------------------- GET -------------------

    /**
     * Listar todos los módulos para vista resumen
     */
    @GetMapping("/list")
    public ResponseEntity<List<ModuleSummaryDTO>> listAll() {
        List<Module> modules = moduleService.list();
        List<ModuleSummaryDTO> dtos = modules.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Listar módulos paginados con offset
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<ModuleSummaryDTO>> paginatedList(
            @RequestParam int offsetOrderNumber,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderNumber").ascending());
        Page<ModuleSummaryDTO> pageDTO = moduleService.paginatedList(offsetOrderNumber, pageable);
        return ResponseEntity.ok(pageDTO);
    }

    /**
     * Listar módulos paginados filtrando por modo
     */
    @GetMapping("/mode")
    public ResponseEntity<Page<ModuleSummaryDTO>> paginateByMode(
            @RequestParam Mode mode,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderNumber").ascending());
        Page<ModuleSummaryDTO> pageDTO = moduleService.paginateByMode(mode, pageable);
        return ResponseEntity.ok(pageDTO);
    }

    /**
     * Listar módulos filtrados por tags
     */
    @GetMapping("/tags")
    public ResponseEntity<Page<ModuleSummaryDTO>> listByTags(
            @RequestParam List<Integer> tagIds,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ModuleSummaryDTO> pageDTO = moduleService.listByTags(tagIds, pageable);
        return ResponseEntity.ok(pageDTO);
    }

    /**
     * Obtener detalle completo de un módulo por id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModuleDetailDTO> getModuleDetail(@PathVariable int id) {
        Module module = moduleService.listId(id);
        if (module == null) {
            return ResponseEntity.notFound().build();
        }
        ModuleDetailDTO dto = convertToDetailDTO(module);
        return ResponseEntity.ok(dto);
    }

    @Autowired
    private IUserModuleRepo umR;
    @GetMapping("/mycourses/{userId}")
    public ResponseEntity<List<ModuleSummaryDTO>> getMyCourses(@PathVariable int userId) {
        List<ModuleSummaryDTO> moduleDTOs = mS.findSummaryModulesByUserId(userId);

        if (moduleDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(moduleDTOs);
    }

    @GetMapping("/mycourses/{userId}/{courseId}")
    public ResponseEntity<MyModuleDTO> listIdmycourses(
            @PathVariable("userId") int userId,
            @PathVariable("moduleId") int moduleId) {

        // Verificar que el usuario tiene acceso a ese modulo
        boolean hasAccess = umR.existsByUserProfileUserIdAndModuleModuleId(userId, moduleId);
        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Module module = mS.listId(moduleId);
        if (module == null) {
            return ResponseEntity.notFound().build();
        }

        ModelMapper modelMapper = new ModelMapper();
        MyModuleDTO moduleDTO = modelMapper.map(module, MyModuleDTO.class);

        if (module.getStudyPlans() != null) {
            List<StudyPlanDTO> studyPlanDTOs = module.getStudyPlans().stream().map(studyPlan -> {
                StudyPlanDTO studyPlanDTO = new StudyPlanDTO();
                studyPlanDTO.setStudyplanId(studyPlan.getStudyplanId());
                studyPlanDTO.setUnit(studyPlan.getUnit());
                studyPlanDTO.setHours(studyPlan.getHours());
                studyPlanDTO.setSessions(studyPlan.getSessions());
                studyPlanDTO.setOrderNumber(studyPlan.getOrderNumber());
                studyPlanDTO.setModuleId(module.getModuleId());
                studyPlanDTO.setUrlrecording(studyPlan.getUrlrecording());
                studyPlanDTO.setDmaterial(studyPlan.getDmaterial());
                studyPlanDTO.setViewpresentation(studyPlan.getViewpresentation());
                return studyPlanDTO;
            }).collect(Collectors.toList());
            moduleDTO.setStudyPlans(studyPlanDTOs);
        }

        return ResponseEntity.ok(moduleDTO);
    }

    // ------------------- DELETE -------------------

    /**
     * Eliminar módulo por id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModule(@PathVariable int id) {
        Module module = moduleService.listId(id);
        if (module == null) {
            return ResponseEntity.notFound().build();
        }
        moduleService.delete(id);
        return ResponseEntity.ok("Módulo eliminado correctamente");
    }

    // ------------------- Conversores auxiliares -------------------

    private ModuleSummaryDTO convertToSummaryDTO(Module module) {
        return ModuleSummaryDTO.builder()
                .moduleId(module.getModuleId())
                .courseTitle(module.getCourse() != null ? module.getCourse().getTitle() : null)
                .programTitle(module.getProgramTitle())
                .description(module.getDescription())
                .brochureUrl(module.getBrochureUrl())
                .startDate(module.getStartDate())
                .certificateHours(module.getCertificateHours())
                .priceRegular(module.getPriceRegular())
                .discountPercentage(module.getDiscountPercentage())
                .promptPaymentPrice(module.getPromptPaymentPrice())
                .isOnSale(module.getIsOnSale())
                .principalImage(module.getPrincipalImage())
                .orderNumber(module.getOrderNumber())
                .mode(module.getMode())
                .urlName(module.getUrlName())
                .build();
    }

    private ModuleDetailDTO convertToDetailDTO(Module module) {
        return ModuleDetailDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getCourse() != null ? module.getCourse().getTitle() : null)
                .programTitle(module.getProgramTitle())
                .description(module.getDescription())
                .brochureUrl(module.getBrochureUrl())
                .whatsappGroupLink(module.getWhatsappGroupLink())
                .startDate(module.getStartDate())
                .certificateHours(module.getCertificateHours())
                .priceRegular(module.getPriceRegular())
                .discountPercentage(module.getDiscountPercentage())
                .promptPaymentPrice(module.getPromptPaymentPrice())
                .isOnSale(module.getIsOnSale())
                .achievement(module.getAchievement())
                .principalImage(module.getPrincipalImage())
                .orderNumber(module.getOrderNumber())
                .mode(module.getMode())
                .urlName(module.getUrlName())
                .benefits(module.getBenefits().stream().map(this::convertToBenefitDTO).collect(Collectors.toList()))
                .tools(module.getTools().stream().map(this::convertToToolDTO).collect(Collectors.toList()))
                .studyPlans(module.getStudyPlans().stream().map(this::convertToStudyPlanDTO).collect(Collectors.toList()))
                .coupons(module.getCoupons().stream().map(this::convertToCouponDTO).collect(Collectors.toList()))
                .freqquests(module.getFreqquests().stream().map(this::convertToFreqQuestDTO).collect(Collectors.toList()))
                .tags(module.getTags().stream().map(this::convertToCourseTagDTO).collect(Collectors.toList()))
                .certificates(module.getCertificates().stream().map(this::convertToCertificateDTO).collect(Collectors.toList()))
                .urlMaterialAccess(module.getUrlMaterialAccess())
                .urlJoinClass(module.getUrlJoinClass())
                .build();
    }

    private MyModuleDTO convertToMyModuleDTO(Module module) {
        return MyModuleDTO.builder()
                .moduleId(module.getModuleId())
                .title(module.getCourse() != null ? module.getCourse().getTitle() : null)
                .programTitle(module.getProgramTitle())
                .description(module.getDescription())
                .whatsappGroupLink(module.getWhatsappGroupLink())
                .studyPlans(module.getStudyPlans().stream().map(this::convertToStudyPlanDTO).collect(Collectors.toList()))
                .urlMaterialAccess(module.getUrlMaterialAccess())
                .urlJoinClass(module.getUrlJoinClass())
                .certificates(module.getCertificates().stream().map(this::convertToCertificateDTO).collect(Collectors.toList()))
                .build();
    }

    private ToolDTO convertToToolDTO(Tool tool) {
        return ToolDTO.builder()
                .toolId(tool.getToolId())
                .name(tool.getName())
                .picture(tool.getPicture())
                .build();
    }

    private StudyPlanDTO convertToStudyPlanDTO(StudyPlan sp) {
        return StudyPlanDTO.builder()
                .studyplanId(sp.getStudyplanId())
                .unit(sp.getUnit())
                .hours(sp.getHours())
                .sessions(sp.getSessions())
                .orderNumber(sp.getOrderNumber())
                .urlrecording(sp.getUrlrecording())
                .dmaterial(sp.getDmaterial())
                .viewpresentation(sp.getViewpresentation())
                .build();
    }

    private CouponDTO convertToCouponDTO(Coupon coupon) {
        return CouponDTO.builder()
                .couponId(coupon.getCouponId())
                .name(coupon.getName())
                .discount(coupon.getDiscount())
                .build();
    }

    private moduleBenefitsDTO convertToBenefitDTO(ModuleBenefits benefit) {
        return moduleBenefitsDTO.builder()
                .id(benefit.getId())
                .benefits(benefit.getBenefits())
                .build();
    }

    private FreqQuestDTO convertToFreqQuestDTO(FreqQuest fq) {
        return FreqQuestDTO.builder()
                .freqquestId(fq.getFreqquestId())
                .questionText(fq.getQuestionText())
                .answerText(fq.getAnswerText())
                .build();
    }

    private CourseTagDTO convertToCourseTagDTO(CourseTag tag) {
        return CourseTagDTO.builder()
                .courseTagId(tag.getCourseTagId())
                .courseTagName(tag.getCourseTagName())
                .build();
    }

    private CertificateDTO convertToCertificateDTO(Certificate cert) {
        return CertificateDTO.builder()
                .id(cert.getId())
                .name(cert.getName())
                .description(cert.getDescription())
                .url(cert.getUrl())
                .build();
    }


}
