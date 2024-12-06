package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseDTO;
import com.aecode.webcoursesback.dtos.FreqQuestDTO;
import com.aecode.webcoursesback.dtos.ToolDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.FreqQuest;
import com.aecode.webcoursesback.entities.Tool;
import com.aecode.webcoursesback.services.ICourseService;
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
@RequestMapping("/course")
public class CourseController  {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ICourseService cS;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "principalImage", required = false) MultipartFile principalImage,
            @RequestPart(value = "data", required = true) String dtoJson) {
        try {
            // Convertir el JSON recibido a un DTO
            ObjectMapper objectMapper = new ObjectMapper();
            CourseDTO dto = objectMapper.readValue(dtoJson, CourseDTO.class);

            // Crear directorio para guardar imágenes basado en el ID del curso
            String userUploadDir = uploadDir + File.separator + "course";
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Variables para guardar los nombres de archivo
            String coverImageFilename = null;
            String principalImageFilename = null;

            // Manejo del archivo de portada (coverImage)
            if (coverImage != null && !coverImage.isEmpty()) {
                coverImageFilename = coverImage.getOriginalFilename();
                byte[] bytes = coverImage.getBytes();
                Path path = userUploadPath.resolve(coverImageFilename);
                Files.write(path, bytes);
            }

            // Manejo del archivo de imagen principal (principalImage)
            if (principalImage != null && !principalImage.isEmpty()) {
                principalImageFilename = principalImage.getOriginalFilename();
                byte[] bytes = principalImage.getBytes();
                Path path = userUploadPath.resolve(principalImageFilename);
                Files.write(path, bytes);
            }

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            Course courses = modelMapper.map(dto, Course.class);

            // Asociar herramientas al curso
            if (dto.getToolIds() != null) {
                List<Tool> tools = dto.getToolIds().stream()
                        .map(toolId -> {
                            Tool tool = new Tool();
                            tool.setToolId(toolId); // Solo asignamos el ID aquí
                            return tool;
                        }).collect(Collectors.toList());
                courses.setTools(tools);
            }

            // Asociar preguntas frecuentes al curso
            if (dto.getFreqquestIds() != null) {
                List<FreqQuest> freqquests = dto.getFreqquestIds().stream()
                        .map(freqquestId -> {
                            FreqQuest freqQuest = new FreqQuest();
                            freqQuest.setFreqquestId(freqquestId); // Solo asignamos el ID aquí
                            return freqQuest;
                        }).collect(Collectors.toList());
                courses.setFreqquests(freqquests);
            }

            // Establecer las rutas de las imágenes en la entidad
            if (coverImageFilename != null) {
                courses.setCoverimage("/uploads/course/" + coverImageFilename);
            }
            if (principalImageFilename != null) {
                courses.setPrincipalimage("/uploads/course/" + principalImageFilename);
            }

            // Guardar el curso
            cS.insert(courses);

            return ResponseEntity.ok("Curso guardado correctamente con imágenes");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar las imágenes: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el curso en la base de datos: " + e.getMessage());
        }
    }


    @GetMapping
    public List<CourseDTO> list() {
        return cS.list().stream().map(course -> {
            ModelMapper modelMapper = new ModelMapper();
            CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);

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

            return courseDTO;
        }).collect(Collectors.toList());
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public CourseDTO listId(@PathVariable("id") Integer id) {
        // Obtener el curso por ID desde el servicio
        Course course = cS.listId(id);

        // Verificar si el curso es nulo
        if (course == null) {
            // Devolver error o respuesta vacía si no se encuentra el curso
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }

        // Convertir la entidad Course a CourseDTO
        ModelMapper modelMapper = new ModelMapper();
        CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);

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

        return courseDTO;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "principalImage", required = false) MultipartFile principalImage,
            @RequestPart(value = "data", required = false) String courseDTOJson) {
        try {
            // Obtener el curso existente por ID
            Course existingCourse = cS.listId(id);
            if (existingCourse == null || existingCourse.getCourseId() == 0) {
                return ResponseEntity.status(404).body("Curso no encontrado");
            }

            // Procesar los datos JSON del DTO si están presentes
            if (courseDTOJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                CourseDTO courseDTO = objectMapper.readValue(courseDTOJson, CourseDTO.class);

                if (courseDTO.getTitle() != null) {
                    existingCourse.setTitle(courseDTO.getTitle());
                }
                if (courseDTO.getDescription() != null) {
                    existingCourse.setDescription(courseDTO.getDescription());
                }
                if (courseDTO.getPrice() != null) {
                    existingCourse.setPrice(courseDTO.getPrice());
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

                // Actualizar las herramientas del curso
                if (courseDTO.getToolIds() != null) {
                    List<Tool> tools = courseDTO.getToolIds().stream()
                            .map(toolId -> {
                                Tool tool = new Tool();
                                tool.setToolId(toolId); // Solo asignamos el ID aquí
                                return tool;
                            }).collect(Collectors.toList());
                    existingCourse.setTools(tools);
                }

                // Actualizar las preguntas frecuentes del curso
                if (courseDTO.getFreqquestIds() != null) {
                    List<FreqQuest> freqquests = courseDTO.getFreqquestIds().stream()
                            .map(freqquestId -> {
                                FreqQuest freqQuest = new FreqQuest();
                                freqQuest.setFreqquestId(freqquestId); // Solo asignamos el ID aquí
                                return freqQuest;
                            }).collect(Collectors.toList());
                    existingCourse.setFreqquests(freqquests);
                }
            }


            // Crear directorio para guardar imágenes basado en el ID del curso
            String userUploadDir = uploadDir + File.separator + "course" + File.separator + id;
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Actualizar la imagen de portada (coverImage)
            if (coverImage != null && !coverImage.isEmpty()) {
                String coverImageFilename = coverImage.getOriginalFilename();
                byte[] bytes = coverImage.getBytes();
                Path path = userUploadPath.resolve(coverImageFilename);
                Files.write(path, bytes);

                // Establecer la nueva ruta en la entidad
                existingCourse.setCoverimage("/uploads/course/" + id + "/" + coverImageFilename);
            }

            // Actualizar la imagen principal (principalImage)
            if (principalImage != null && !principalImage.isEmpty()) {
                String principalImageFilename = principalImage.getOriginalFilename();
                byte[] bytes = principalImage.getBytes();
                Path path = userUploadPath.resolve(principalImageFilename);
                Files.write(path, bytes);

                // Establecer la nueva ruta en la entidad
                existingCourse.setPrincipalimage("/uploads/course/" + id + "/" + principalImageFilename);
            }

            // Guardar los cambios
            cS.insert(existingCourse);

            return ResponseEntity.ok("Curso actualizado correctamente con imágenes");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al guardar las imágenes: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el curso: " + e.getMessage());
        }
    }


}
