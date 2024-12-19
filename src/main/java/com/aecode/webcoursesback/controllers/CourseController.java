package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseDTO;
import com.aecode.webcoursesback.dtos.SecondCourseDTO;
import com.aecode.webcoursesback.dtos.ToolDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.Tool;
import com.aecode.webcoursesback.services.ICourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestPart(value = "data", required = true) String dtoJson) {
        try {
            // Convertir el JSON recibido a un DTO
            ObjectMapper objectMapper = new ObjectMapper();
            CourseDTO dto = objectMapper.readValue(dtoJson, CourseDTO.class);

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            Course courses = modelMapper.map(dto, Course.class);
            cS.insert(courses);

            // Crear directorio para guardar imágenes basado en el ID del curso
            String userUploadDir = uploadDir + File.separator + "course"+ File.separator + dto.getCourseId();
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Variables para guardar los nombres de archivo
            String coverImageFilename = null;

            // Manejo del archivo de portada (coverImage)
            if (coverImage != null && !coverImage.isEmpty()) {
                coverImageFilename = coverImage.getOriginalFilename();
                byte[] bytes = coverImage.getBytes();
                Path path = userUploadPath.resolve(coverImageFilename);
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

            // Establecer las rutas de las imágenes en la entidad
            if (coverImageFilename != null) {
                courses.setCoverimage("/uploads/course/"+courses.getCourseId() +"/"+ coverImageFilename);
            }
                // Guardar el curso
                cS.insert(courses);

                return ResponseEntity.ok("Curso guardado correctamente con imágenes");
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping
    public List<CourseDTO> list() {
        return cS.list().stream().map(x -> {
            ModelMapper modelMapper = new ModelMapper();
            CourseDTO courseDTO = modelMapper.map(x, CourseDTO.class);

            if (x.getTools() != null) {
                List<ToolDTO> toolDTOs = x.getTools().stream().map(tool -> {
                    ToolDTO toolDTO = new ToolDTO();
                    toolDTO.setToolId(tool.getToolId());
                    toolDTO.setName(tool.getName());
                    toolDTO.setPicture(tool.getPicture());
                    return toolDTO;
                }).collect(Collectors.toList());
                courseDTO.setTools(toolDTOs);
            }


            return modelMapper.map(x, CourseDTO.class);
        }).collect(Collectors.toList());
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public CourseDTO listId(@PathVariable("id") Integer id) {
        ModelMapper m=new ModelMapper();
        CourseDTO dto=m.map(cS.listId(id),CourseDTO.class);
        return dto;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
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
                if(courseDTO.getVideoUrl()!=null){
                    existingCourse.setVideoUrl(courseDTO.getVideoUrl());
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
                if(courseDTO.getHours()!=0){
                    existingCourse.setHours(courseDTO.getHours());
                }
                if(courseDTO.getPrice()!=0) {
                    existingCourse.setPrice(courseDTO.getPrice());
                }
                if(courseDTO.getPercentage()!=0) {
                    existingCourse.setPercentage(courseDTO.getPercentage());
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
