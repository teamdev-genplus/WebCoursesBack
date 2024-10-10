package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.ClassDTO;
import com.aecode.webcoursesback.dtos.ClassQuestionDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IClassService;
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
@RequestMapping("/class")
public class ClassController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private IClassService cS;

    @Autowired
    private IUserProfileService upS;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(@RequestPart(value="file", required = false) MultipartFile imagen,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            ClassDTO dto= objectMapper.readValue(dtoJson, ClassDTO.class);

            String userUploadDir = uploadDir + File.separator + "class";
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

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            Class classes = modelMapper.map(dto, Class.class);
            // Establecer la ruta del archivo en la entidad
            classes.setDocument("class/"+originalFilename);
            cS.insert(classes);

            return ResponseEntity.ok("Clase guardado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el objeto en la base de datos: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> list(@RequestParam String email) {
        // Verificar si el usuario tiene acceso
        UserProfile user = upS.findByEmail(email);
        if (user == null || !user.isHasAccess()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: No access to classes.");
        }

        // Si tiene acceso, devolver las clases
        List<ClassDTO> datos = cS.list().stream()
                .map(classes -> {
                    ClassDTO dto = new ClassDTO();
                    dto.setClassId(classes.getClassId());
                    dto.setModuleId(classes.getModule().getModuleId());
                    dto.setTitle(classes.getTitle());
                    dto.setVideoUrl(classes.getVideoUrl());
                    dto.setDescription(classes.getDescription());
                    dto.setDurationMinutes(classes.getDurationMinutes());
                    dto.setOrderNumber(classes.getOrderNumber());
                    dto.setDocument("/uploads/" + classes.getDocument());

                    dto.setClassquestions(classes.getClassquestions().stream()
                            .map(classQuestion -> {
                                ClassQuestionDTO questionDTO = new ClassQuestionDTO();
                                questionDTO.setQuestionId(classQuestion.getQuestionId());
                                return questionDTO;
                            })
                            .collect(Collectors.toSet()));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(datos);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public ClassDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        ClassDTO dto=m.map(cS.listId(id),ClassDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody ClassDTO dto) {
        ModelMapper m = new ModelMapper();
        Class c = m.map(dto, Class.class);
        cS.insert(c);
    }


}
