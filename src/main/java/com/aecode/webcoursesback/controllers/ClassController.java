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
    public List<ClassDTO> list() {
        return cS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, ClassDTO.class);
        }).collect(Collectors.toList());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public ClassDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        ClassDTO dto=m.map(cS.listId(id),ClassDTO.class);
        return dto;
    }
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(@RequestPart(value="file", required = false) MultipartFile document,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {
            // Convertir el JSON a ClassDTO
            ObjectMapper objectMapper = new ObjectMapper();
            ClassDTO dto = objectMapper.readValue(dtoJson, ClassDTO.class);

            // Obtener la clase existente desde la base de datos
            Class existingClass = cS.listId(dto.getClassId());
            if (existingClass == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clase no encontrada");
            }

            String userUploadDir = uploadDir + File.separator + "class";
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Manejo del archivo de documento
            if (document != null && !document.isEmpty()) {
                // Si se sube un nuevo documento, reemplazar el documento actual
                originalFilename = document.getOriginalFilename();
                byte[] bytes = document.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);

                // Actualizar la ruta del documento en la entidad
                existingClass.setDocument("class/" + originalFilename);
            }

            // Actualizar otros datos de la clase
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(dto, existingClass);  // Mapear solo los cambios del DTO a la clase existente

            // Guardar los cambios en la base de datos
            cS.insert(existingClass);

            return ResponseEntity.ok("Clase actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de documento: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la clase: " + e.getMessage());
        }
    }



}
