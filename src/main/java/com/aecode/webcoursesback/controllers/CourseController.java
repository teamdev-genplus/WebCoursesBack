package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private IUserProfileService upS;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(@RequestPart(value="file", required = false) MultipartFile imagen,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CourseDTO dto= objectMapper.readValue(dtoJson, CourseDTO.class);

            String userUploadDir = uploadDir + File.separator + "course";
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Manejo del archivo de script
            if (imagen != null && !imagen.isEmpty()) {
                originalFilename = imagen.getOriginalFilename();;
                byte[] bytes = imagen.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);
            }

            //Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            Course course = modelMapper.map(dto, Course.class);
            //Establecer la ruta del archivo en la entidad
            course.setImage("course/"+originalFilename);
            cS.insert(course);

            return ResponseEntity.ok("Curso guardado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el objeto en la base de datos: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam String email) {
        // Buscar al usuario por su email
        UserProfile user = upS.findByEmail(email);
        // Verificar si el usuario tiene acceso
        if (user == null || !user.isHasAccess()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: No access to courses.");
        }
        ModelMapper modelMapper = new ModelMapper();
        List<CourseDTO> datos = cS.list().stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))  // Mapeo directo con ModelMapper
                .collect(Collectors.toList());
        return ResponseEntity.ok(datos);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public CourseDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        CourseDTO dto=m.map(cS.listId(id),CourseDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody CourseDTO dto) {
        ModelMapper m = new ModelMapper();
        Course c = m.map(dto, Course.class);
        cS.insert(c);
    }
}
