package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.FreqQuest;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.entities.Tool;
import com.aecode.webcoursesback.services.ISecondCourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/secondarycourses")
public class SecondCourseController {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ISecondCourseService scS;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(
            @RequestPart(value = "principalImage", required = false) MultipartFile principalImage,
            @RequestPart(value = "data", required = true) String dtoJson) {
        try {
            // Convertir el JSON recibido a un DTO
            ObjectMapper objectMapper = new ObjectMapper();
            SecondCourseDTO dto = objectMapper.readValue(dtoJson, SecondCourseDTO.class);

            ModelMapper modelMapper = new ModelMapper();
            SecondaryCourses courses = modelMapper.map(dto, SecondaryCourses.class);
            scS.insert(courses);
            // Crear directorio para guardar imágenes basado en el ID del curso
            String userUploadDir = uploadDir + File.separator + "secondcourse"+ File.separator + dto.getSeccourseId();
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Variables para guardar los nombres de archivo
            String principalImageFilename = null;
            // Manejo del archivo de imagen principal (principalImage)
            if (principalImage != null && !principalImage.isEmpty()) {
                principalImageFilename = principalImage.getOriginalFilename();
                byte[] bytes = principalImage.getBytes();
                Path path = userUploadPath.resolve(principalImageFilename);
                Files.write(path, bytes);
            }

            // Asociar herramientas al curso
            if (dto.getToolIds() != null) {
                List<Tool> tools = dto.getToolIds().stream()
                        .map(toolId -> {
                            Tool tool = new Tool();
                            tool.setToolId(toolId);
                            return tool;
                        }).collect(Collectors.toList());
                courses.setTools(tools);
            }

            // Asociar preguntas frecuentes al curso
            if (dto.getFreqquestIds() != null) {
                List<FreqQuest> freqquests = dto.getFreqquestIds().stream()
                        .map(freqquestId -> {
                            FreqQuest freqQuest = new FreqQuest();
                            freqQuest.setFreqquestId(freqquestId);
                            return freqQuest;
                        }).collect(Collectors.toList());
                courses.setFreqquests(freqquests);
            }

            if (principalImageFilename != null) {
                courses.setPrincipalimage("/uploads/secondcourse/"+courses.getSeccourseId()+"/" + principalImageFilename);
            }

            // Guardar el curso
            scS.insert(courses);

            return ResponseEntity.ok("Curso guardado correctamente con imágenes");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar las imágenes: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el curso en la base de datos: " + e.getMessage());
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
                    // Asignar el ID del curso secundario al DTO
                    studyPlanDTO.setSeccourseId(course.getSeccourseId());
                    return studyPlanDTO;
                }).collect(Collectors.toList());
                courseDTO.setStudyplans(studyPlanDTOs);
            }
            if(course.getCoupons() != null) {
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
    public void delete(@PathVariable("id")Integer id){scS.delete(id);}

    @GetMapping("/{id}")
    public SecondCourseDTO listId(@PathVariable("id") Integer id) {
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
                studyPlanDTO.setSeccourseId(course.getSeccourseId());
                return studyPlanDTO;
            }).collect(Collectors.toList());
            courseDTO.setStudyplans(studyPlanDTOs);
        }
        if(course.getCoupons() != null) {
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
            @PathVariable("id") Integer id,
            @RequestPart(value = "principalImage", required = false) MultipartFile principalImage,
            @RequestPart(value = "data", required = false) String courseDTOJson) {
        try {
            // Obtener el curso existente por ID
            SecondaryCourses existingCourse = scS.listId(id);
            if (existingCourse == null || existingCourse.getSeccourseId() == 0) {
                return ResponseEntity.status(404).body("Curso no encontrado");
            }

            // Procesar los datos JSON del DTO si están presentes
            if (courseDTOJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                SecondCourseDTO courseDTO = objectMapper.readValue(courseDTOJson, SecondCourseDTO.class);

                if (courseDTO.getTitle() != null) {
                    existingCourse.setTitle(courseDTO.getTitle());
                }
                if (courseDTO.getDescription() != null) {
                    existingCourse.setDescription(courseDTO.getDescription());
                }
                if (courseDTO.getPriceRegular() != null) {
                    existingCourse.setPriceRegular(courseDTO.getPriceRegular());
                }
                if(courseDTO.getPriceAcademy()!=null){
                    existingCourse.setPriceAcademy(courseDTO.getPriceAcademy());
                }
                if (courseDTO.getLevel() != null) {
                    existingCourse.setLevel(courseDTO.getLevel());
                }
                if (courseDTO.getMode() != null) {
                    existingCourse.setMode(courseDTO.getMode());
                }
                if (courseDTO.getBenefits() != null) {
                    existingCourse.setBenefits(courseDTO.getBenefits());
                }
                if (courseDTO.getSchedule() != null) {
                    existingCourse.setSchedule(courseDTO.getSchedule());
                }
                if (courseDTO.getVideoUrl() != null) {
                    existingCourse.setVideoUrl(courseDTO.getVideoUrl());
                }
                if (courseDTO.getAchievement() != null) {
                    existingCourse.setAchievement(courseDTO.getAchievement());
                }
                if(courseDTO.getExterallink()!=null){
                    existingCourse.setExterallink(courseDTO.getExterallink());
                }
                if(courseDTO.getPercentage()!=0){
                    existingCourse.setPercentage(courseDTO.getPercentage());
                }

                // Actualizar las herramientas del curso
                if (courseDTO.getToolIds() != null) {
                    List<Tool> tools = courseDTO.getToolIds().stream()
                            .map(toolId -> {
                                Tool tool = new Tool();
                                tool.setToolId(toolId);
                                return tool;
                            }).collect(Collectors.toList());
                    existingCourse.setTools(tools);
                }

                // Actualizar las preguntas frecuentes del curso
                if (courseDTO.getFreqquestIds() != null) {
                    List<FreqQuest> freqquests = courseDTO.getFreqquestIds().stream()
                            .map(freqquestId -> {
                                FreqQuest freqQuest = new FreqQuest();
                                freqQuest.setFreqquestId(freqquestId);
                                return freqQuest;
                            }).collect(Collectors.toList());
                    existingCourse.setFreqquests(freqquests);
                }
            }


            // Crear directorio para guardar imágenes basado en el ID del curso
            String userUploadDir = uploadDir + File.separator + "secondcourse" + File.separator + id;
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Actualizar la imagen principal (principalImage)
            if (principalImage != null && !principalImage.isEmpty()) {
                String principalImageFilename = principalImage.getOriginalFilename();
                byte[] bytes = principalImage.getBytes();
                Path path = userUploadPath.resolve(principalImageFilename);
                Files.write(path, bytes);
                // Establecer la nueva ruta en la entidad
                existingCourse.setPrincipalimage("/uploads/secondcourse/" + id + "/" + principalImageFilename);
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

}
