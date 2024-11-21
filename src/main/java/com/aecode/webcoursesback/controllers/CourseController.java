package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseDTO;
import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.services.ICourseService;import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
public class CourseController  {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ICourseService cS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody CourseDTO dto) {
        ModelMapper m = new ModelMapper();
        Course c = m.map(dto, Course.class);
        cS.insert(c);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<CourseDTO> list() {
        return cS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, CourseDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public CourseDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        CourseDTO dto=m.map(cS.listId(id),CourseDTO.class);
        return dto;
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody CourseDTO courseDTO) {
        try {
            // Obtener el curso existente por ID
            Course existingCourse = cS.listId(id);
            if (existingCourse == null || existingCourse.getCourseId() == 0) {
                return ResponseEntity.status(404).body("Curso no encontrado");
            }

            // Actualizar solo los campos proporcionados en el DTO
            if (courseDTO.getTitle() != null) {
                existingCourse.setTitle(courseDTO.getTitle());
            }
            if (courseDTO.getTag() != null) {
                existingCourse.setTag(courseDTO.getTag());
            }
            if (courseDTO.getVideoUrl() != null) {
                existingCourse.setVideoUrl(courseDTO.getVideoUrl());
            }

            // Guardar los cambios
            cS.insert(existingCourse);

            return ResponseEntity.ok("Curso actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el curso: " + e.getMessage());
        }
    }

}
