package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.CourseTag;
import com.aecode.webcoursesback.entities.FreqQuest;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.entities.Tool;
import com.aecode.webcoursesback.services.ISecondCourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/secondarycourses")
public class SecondCourseController {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ISecondCourseService scS;

    private final ObjectMapper objMapper;

    public SecondCourseController(ObjectMapper objectMapper) {
        this.objMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(
            @RequestPart(value = "data", required = true) String dtoJson) {
        try {
            SecondCourseDTO dto = objMapper.readValue(dtoJson, SecondCourseDTO.class);

            ModelMapper modelMapper = new ModelMapper();
            SecondaryCourses courses = modelMapper.map(dto, SecondaryCourses.class);
            scS.insert(courses);

            // Asociar herramientas al curso
            if (dto.getTools() != null) {
                List<Tool> tools = dto.getTools().stream()
                        .map(toolId -> {
                            Tool tool = new Tool();
                            tool.setToolId(toolId.getToolId());
                            return tool;
                        }).collect(Collectors.toList());
                courses.setTools(tools);
            }

            // Asociar preguntas frecuentes al curso
            if (dto.getFreqquests() != null) {
                List<FreqQuest> freqquests = dto.getFreqquests().stream()
                        .map(freqquestId -> {
                            FreqQuest freqQuest = new FreqQuest();
                            freqQuest.setFreqquestId(freqquestId.getFreqquestId());
                            return freqQuest;
                        }).collect(Collectors.toList());
                courses.setFreqquests(freqquests);
            }

            // Guardar el curso
            scS.insert(courses);

            return ResponseEntity.ok("Curso guardado correctamente con imágenes");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar las imágenes: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al insertar el curso en la base de datos: " + e.getMessage());
        }
    }

    @GetMapping
    public List<SecondCourseDTO> list() {
        return scS.list().stream().map(course -> {
            ModelMapper modelMapper = new ModelMapper();
            SecondCourseDTO courseDTO = modelMapper.map(course, SecondCourseDTO.class);

            if (course.getTools() != null) {
                List<ToolDTO> toolDTOs = course.getTools().stream().map(tool -> {
                    ToolDTO toolDTO = new ToolDTO();
                    toolDTO.setToolId(tool.getToolId());
                    toolDTO.setName(tool.getName());
                    toolDTO.setPicture(tool.getPicture());
                    return toolDTO;
                }).collect(Collectors.toList());
                courseDTO.setTools(toolDTOs);
            }

            if (course.getFreqquests() != null) {
                List<FreqQuestDTO> freqQuestDTOs = course.getFreqquests().stream().map(freqQuest -> {
                    FreqQuestDTO freqQuestDTO = new FreqQuestDTO();
                    freqQuestDTO.setFreqquestId(freqQuest.getFreqquestId());
                    freqQuestDTO.setQuestionText(freqQuest.getQuestionText());
                    freqQuestDTO.setAnswerText(freqQuest.getAnswerText());
                    return freqQuestDTO;
                }).collect(Collectors.toList());
                courseDTO.setFreqquests(freqQuestDTOs);
            }
            if (course.getStudyplans() != null) {
                List<StudyPlanDTO> studyPlanDTOs = course.getStudyplans().stream().map(studyPlan -> {
                    StudyPlanDTO studyPlanDTO = new StudyPlanDTO();
                    studyPlanDTO.setStudyplanId(studyPlan.getStudyplanId());
                    studyPlanDTO.setUnit(studyPlan.getUnit());
                    studyPlanDTO.setHours(studyPlan.getHours());
                    studyPlanDTO.setSessions(studyPlan.getSessions());
                    studyPlanDTO.setOrderNumber(studyPlan.getOrderNumber());
                    // Asignar el ID del curso secundario al DTO
                    studyPlanDTO.setSeccourseId(course.getSeccourseId());
                    return studyPlanDTO;
                }).collect(Collectors.toList());
                courseDTO.setStudyplans(studyPlanDTOs);
            }
            if (course.getCoupons() != null) {
                List<CouponDTO> couponDTOs = course.getCoupons().stream().map(coupon -> {
                    CouponDTO couponDTO = new CouponDTO();
                    couponDTO.setCouponId(coupon.getCouponId());
                    couponDTO.setName(coupon.getName());
                    couponDTO.setDiscount(coupon.getDiscount());
                    return couponDTO;
                }).collect(Collectors.toList());
                courseDTO.setCoupons(couponDTOs);
            }

            return courseDTO;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        scS.delete(id);
    }

    @GetMapping("/{id}")
    public SecondCourseDTO listId(@PathVariable("id") Long id) {
        // Obtener el curso por ID desde el servicio
        SecondaryCourses course = scS.listId(id);

        // Verificar si el curso es nulo
        if (course == null) {
            // Devolver error o respuesta vacía si no se encuentra el curso
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }

        // Convertir la entidad Course a CourseDTO
        ModelMapper modelMapper = new ModelMapper();
        SecondCourseDTO courseDTO = modelMapper.map(course, SecondCourseDTO.class);

        // Mapear herramientas (Tool -> ToolDTO)
        if (course.getTools() != null) {
            List<ToolDTO> toolDTOs = course.getTools().stream().map(tool -> {
                ToolDTO toolDTO = new ToolDTO();
                toolDTO.setToolId(tool.getToolId());
                toolDTO.setName(tool.getName());
                toolDTO.setPicture(tool.getPicture());
                return toolDTO;
            }).collect(Collectors.toList());
            courseDTO.setTools(toolDTOs);
        }

        // Mapear preguntas frecuentes (FreqQuest -> FreqQuestDTO)
        if (course.getFreqquests() != null) {
            List<FreqQuestDTO> freqQuestDTOs = course.getFreqquests().stream().map(freqQuest -> {
                FreqQuestDTO freqQuestDTO = new FreqQuestDTO();
                freqQuestDTO.setFreqquestId(freqQuest.getFreqquestId());
                freqQuestDTO.setQuestionText(freqQuest.getQuestionText());
                freqQuestDTO.setAnswerText(freqQuest.getAnswerText());
                return freqQuestDTO;
            }).collect(Collectors.toList());
            courseDTO.setFreqquests(freqQuestDTOs);
        }
        if (course.getStudyplans() != null) {
            List<StudyPlanDTO> studyPlanDTOs = course.getStudyplans().stream().map(studyPlan -> {
                StudyPlanDTO studyPlanDTO = new StudyPlanDTO();
                studyPlanDTO.setStudyplanId(studyPlan.getStudyplanId());
                studyPlanDTO.setUnit(studyPlan.getUnit());
                studyPlanDTO.setHours(studyPlan.getHours());
                studyPlanDTO.setSessions(studyPlan.getSessions());
                studyPlanDTO.setOrderNumber(studyPlan.getOrderNumber());
                studyPlanDTO.setSeccourseId(course.getSeccourseId());
                return studyPlanDTO;
            }).collect(Collectors.toList());
            courseDTO.setStudyplans(studyPlanDTOs);
        }
        if (course.getCoupons() != null) {
            List<CouponDTO> couponDTOs = course.getCoupons().stream().map(coupon -> {
                CouponDTO couponDTO = new CouponDTO();
                couponDTO.setCouponId(coupon.getCouponId());
                couponDTO.setName(coupon.getName());
                couponDTO.setDiscount(coupon.getDiscount());
                couponDTO.setSeccourseId(course.getSeccourseId());
                return couponDTO;
            }).collect(Collectors.toList());
            courseDTO.setCoupons(couponDTOs);
        }

        return courseDTO;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("id") Long id,
            @RequestPart(value = "data", required = false) String courseDTOJson) {
        try {
            SecondaryCourses existingCourse = scS.listId(id);
            if (existingCourse == null || existingCourse.getSeccourseId() == 0) {
                return ResponseEntity.status(404).body("Curso no encontrado");
            }

            // Procesar los datos JSON del DTO si están presentes
            if (courseDTOJson != null) {
                SecondCourseDTO courseDTO = objMapper.readValue(courseDTOJson, SecondCourseDTO.class);

                Optional.ofNullable(courseDTO.getTitle()).ifPresent(existingCourse::setTitle);
                Optional.ofNullable(courseDTO.getDescription()).ifPresent(existingCourse::setDescription);
                Optional.ofNullable(courseDTO.getProgramTitle()).ifPresent(existingCourse::setProgramTitle);
                Optional.ofNullable(courseDTO.getStartDate()).ifPresent(existingCourse::setStartDate);
                Optional.ofNullable(courseDTO.getCertificateHours()).ifPresent(existingCourse::setCertificateHours);
                Optional.ofNullable(courseDTO.getPriceRegular()).ifPresent(existingCourse::setPriceRegular);
                Optional.ofNullable(courseDTO.getDiscountPercentage()).ifPresent(existingCourse::setDiscountPercentage);
                Optional.ofNullable(courseDTO.getPromptPaymentPrice()).ifPresent(existingCourse::setPromptPaymentPrice);
                Optional.ofNullable(courseDTO.getMode())
                        .ifPresent(mode -> existingCourse.setMode(SecondaryCourses.Mode.valueOf(mode.name())));
                Optional.ofNullable(courseDTO.getAchievement()).ifPresent(existingCourse::setAchievement);
                Optional.ofNullable(courseDTO.getVideoUrl()).ifPresent(existingCourse::setVideoUrl);
                Optional.ofNullable(courseDTO.getPrincipalimage()).ifPresent(existingCourse::setPrincipalimage);
                Optional.ofNullable(courseDTO.getTotalHours()).ifPresent(existingCourse::setTotalHours);
                Optional.ofNullable(courseDTO.getNumberOfSessions()).ifPresent(existingCourse::setNumberOfSessions);
                Optional.ofNullable(courseDTO.getNumberOfUnits()).ifPresent(existingCourse::setNumberOfUnits);
                Optional.ofNullable(courseDTO.getOrderNumber())
                        .ifPresent(existingCourse::setOrderNumber);
                Optional.ofNullable(courseDTO.getSchedules()).ifPresent(existingCourse::setSchedules);
                Optional.ofNullable(courseDTO.getRequirements()).ifPresent(existingCourse::setRequirements);

                // Actualizar las herramientas asociadas al curso
                if (courseDTO.getTools() != null) {
                    List<Tool> tools = courseDTO.getTools().stream()
                            .map(toolItem -> {
                                Tool tool = new Tool();
                                tool.setToolId(toolItem.getToolId());
                                return tool;
                            }).collect(Collectors.toList());
                    existingCourse.setTools(tools);
                }

                // Actualizar las preguntas frecuentes asociadas al curso
                if (courseDTO.getFreqquests() != null) {
                    List<FreqQuest> freqquests = courseDTO.getFreqquests().stream()
                            .map(freqquestItem -> {
                                FreqQuest freqQuest = new FreqQuest();
                                freqQuest.setFreqquestId(freqquestItem.getFreqquestId());
                                return freqQuest;
                            }).collect(Collectors.toList());
                    existingCourse.setFreqquests(freqquests);
                }

                if (courseDTO.getTags() != null) {
                    List<CourseTag> tags = courseDTO.getTags().stream()
                            .map(tagItem -> {
                                CourseTag courseTag = new CourseTag();
                                courseTag.setCourseTagId(tagItem.getCourseTagId());
                                return courseTag;
                            }).collect(Collectors.toList());
                    existingCourse.setTags(tags);
                }

                System.out.println("DTO después de la conversión: " + objMapper.writeValueAsString(courseDTO));

            }

            // Guardar los cambios
            scS.insert(existingCourse);

            return ResponseEntity.ok("Curso actualizado correctamente con imágenes");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al guardar las imágenes: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el curso: " + e.getMessage());
        }
    }

    @GetMapping("/getByModulexProgram/{module}/{programTitle}")
    public SecondCourseDTO getCourseByModulexProgram(@PathVariable String module, @PathVariable String programTitle) {
        // Obtener el curso por módulo y programa
        SecondaryCourses course = scS.listByModulexProgram(module, programTitle);

        // Verificar si el curso es nulo
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }

        // Mapear la entidad a DTO
        ModelMapper modelMapper = new ModelMapper();
        SecondCourseDTO courseDTO = modelMapper.map(course, SecondCourseDTO.class);

        // Mapear herramientas
        if (course.getTools() != null) {
            List<ToolDTO> toolDTOs = course.getTools().stream().map(tool -> {
                ToolDTO toolDTO = new ToolDTO();
                toolDTO.setToolId(tool.getToolId());
                toolDTO.setName(tool.getName());
                toolDTO.setPicture(tool.getPicture());
                return toolDTO;
            }).collect(Collectors.toList());
            courseDTO.setTools(toolDTOs);
        }

        // Mapear preguntas frecuentes
        if (course.getFreqquests() != null) {
            List<FreqQuestDTO> freqQuestDTOs = course.getFreqquests().stream().map(freqQuest -> {
                FreqQuestDTO freqQuestDTO = new FreqQuestDTO();
                freqQuestDTO.setFreqquestId(freqQuest.getFreqquestId());
                freqQuestDTO.setQuestionText(freqQuest.getQuestionText());
                freqQuestDTO.setAnswerText(freqQuest.getAnswerText());
                return freqQuestDTO;
            }).collect(Collectors.toList());
            courseDTO.setFreqquests(freqQuestDTOs);
        }

        // Mapear planes de estudio
        if (course.getStudyplans() != null) {
            List<StudyPlanDTO> studyPlanDTOs = course.getStudyplans().stream().map(studyPlan -> {
                StudyPlanDTO studyPlanDTO = new StudyPlanDTO();
                studyPlanDTO.setStudyplanId(studyPlan.getStudyplanId());
                studyPlanDTO.setUnit(studyPlan.getUnit());
                studyPlanDTO.setHours(studyPlan.getHours());
                studyPlanDTO.setSessions(studyPlan.getSessions());
                studyPlanDTO.setOrderNumber(studyPlan.getOrderNumber());
                studyPlanDTO.setSeccourseId(course.getSeccourseId());
                return studyPlanDTO;
            }).collect(Collectors.toList());
            courseDTO.setStudyplans(studyPlanDTOs);
        }

        // Mapear cupones
        if (course.getCoupons() != null) {
            List<CouponDTO> couponDTOs = course.getCoupons().stream().map(coupon -> {
                CouponDTO couponDTO = new CouponDTO();
                couponDTO.setCouponId(coupon.getCouponId());
                couponDTO.setName(coupon.getName());
                couponDTO.setDiscount(coupon.getDiscount());
                couponDTO.setSeccourseId(course.getSeccourseId());
                return couponDTO;
            }).collect(Collectors.toList());
            courseDTO.setCoupons(couponDTOs);
        }

        return courseDTO;
    }

    @GetMapping("/paginatedList")
    public ResponseEntity<Page<SecondCourseSummaryDTO>> paginatedList(
            @RequestParam int offsetCourseId,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderNumber").ascending());
        Page<SecondCourseSummaryDTO> courses = scS.paginatedList(offsetCourseId, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/paginateByMode")
    public ResponseEntity<Page<SecondCourseSummaryDTO>> paginateByMode(
            @RequestParam String mode,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderNumber").ascending());
        return ResponseEntity.ok(scS.paginateByMode(mode, pageable));
    }

    @GetMapping("/paginateByDate")
    public ResponseEntity<Page<SecondCourseSummaryDTO>> paginateByDate(
            @RequestParam int offsetCourseId,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<SecondCourseSummaryDTO> courses = scS.paginatedList(offsetCourseId, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/paginateByTags")
    public ResponseEntity<Page<SecondCourseSummaryDTO>> getCoursesByTags(
            @RequestParam List<Integer> tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SecondCourseSummaryDTO> courses = scS.listByCourseTags(tagIds, pageable);

        return ResponseEntity.ok(courses);
    }

}
