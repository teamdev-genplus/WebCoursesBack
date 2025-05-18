package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.services.IModuleService;
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
@RequestMapping("/module")
public class ModuleController {
    @Autowired
    private IModuleService mS;
    @Autowired
    private ICourseService cS;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(
            @RequestPart(value = "moduleImage", required = false) MultipartFile moduleImage,
            @RequestPart(value = "data", required = true) String moduleDTOJson) {
        try {
            // Convertir el JSON del DTO en un objeto ModuleDTO
            ObjectMapper objectMapper = new ObjectMapper();
            ModuleDTO moduleDTO = objectMapper.readValue(moduleDTOJson, ModuleDTO.class);

            // Crear una instancia de Module usando ModelMapper
            ModelMapper modelMapper = new ModelMapper();
            Module module = modelMapper.map(moduleDTO, Module.class);

            mS.insert(module);

            // Manejar la imagen del módulo
            if (moduleImage != null && !moduleImage.isEmpty()) {
                // Crear directorio para guardar imágenes basado en el ID del curso asociado
                String uploadPath = uploadDir + File.separator + "module" + File.separator + moduleDTO.getCourseId();
                Path moduleUploadPath = Paths.get(uploadPath);
                if (!Files.exists(moduleUploadPath)) {
                    Files.createDirectories(moduleUploadPath);
                }

                // Guardar la imagen
                String moduleImageFilename = moduleImage.getOriginalFilename();
                byte[] bytes = moduleImage.getBytes();
                Path path = moduleUploadPath.resolve(moduleImageFilename);
                Files.write(path, bytes);

                // Establecer la ruta de la imagen en la entidad
                module.setModuleimage("/uploads/module/" + moduleDTO.getCourseId() + "/" + moduleImageFilename);
            }

            // Guardar el módulo en la base de datos
            mS.insert(module);

            return ResponseEntity.status(201).body("Module created successfully with image");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving the module image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating the module: " + e.getMessage());
        }
    }


    @GetMapping
    public List<ModuleDTO> list() {
        return mS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, ModuleDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){mS.delete(id);}

    @GetMapping("/{id}")
    public ModuleDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        ModuleDTO dto=m.map(mS.listId(id),ModuleDTO.class);
        return dto;
    }
//    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> update(
//            @PathVariable("id") Integer id,
//            @RequestPart(value = "moduleImage", required = false) MultipartFile moduleImage,
//            @RequestPart(value = "data", required = false) String moduleDTOJson) {
//        try {
//            // Obtener el módulo existente por ID
//            Module existingModule = mS.listId(id);
//            if (existingModule == null || existingModule.getModuleId() == 0) {
//                return ResponseEntity.status(404).body("Módulo no encontrado");
//            }
//
//            // Procesar los datos JSON del DTO si está presente
//            if (moduleDTOJson != null) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                ModuleDTO moduleDTO = objectMapper.readValue(moduleDTOJson, ModuleDTO.class);
//
//                if (moduleDTO.getTitle() != null) {
//                    existingModule.setTitle(moduleDTO.getTitle());
//                }
//                if (moduleDTO.getVideoUrl() != null) {
//                    existingModule.setVideoUrl(moduleDTO.getVideoUrl());
//                }
//                if (moduleDTO.getOrderNumber() != 0) {
//                    existingModule.setOrderNumber(moduleDTO.getOrderNumber());
//                }
//                if (moduleDTO.getCourseId() != 0) {
//                    Course course = cS.listId(moduleDTO.getCourseId());
//                    if (course == null || course.getCourseId() == 0) {
//                        return ResponseEntity.status(404).body("Curso asociado no encontrado");
//                    }
//                    existingModule.setCourse(course);
//                }
//                if (moduleDTO.getPrice() != 0) {
//                    existingModule.setPrice(moduleDTO.getPrice());
//                }
//                if (moduleDTO.getPercentage() != 0) {
//                    existingModule.setPercentage(moduleDTO.getPercentage());
//                }
//                if (moduleDTO.getHours() != 0) {
//                    existingModule.setHours(moduleDTO.getHours());
//                }
//            }
//
//            // Crear directorio para guardar la imagen basado en el ID del curso asociado si es necesario
//            if (moduleImage != null && !moduleImage.isEmpty()) {
//                String uploadPath = uploadDir + File.separator + "module" + File.separator + id;
//                Path moduleUploadPath = Paths.get(uploadPath);
//                if (!Files.exists(moduleUploadPath)) {
//                    Files.createDirectories(moduleUploadPath);
//                }
//
//                // Guardar la nueva imagen
//                String moduleImageFilename = moduleImage.getOriginalFilename();
//                byte[] bytes = moduleImage.getBytes();
//                Path path = moduleUploadPath.resolve(moduleImageFilename);
//                Files.write(path, bytes);
//
//                // Actualizar la ruta de la imagen en la entidad
//                existingModule.setModuleimage("/uploads/module/" + id + "/" + moduleImageFilename);
//            }
//
//            // Guardar los cambios
//            mS.insert(existingModule);
//
//            return ResponseEntity.ok("Módulo actualizado correctamente");
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Error al guardar la imagen del módulo: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error al actualizar el módulo: " + e.getMessage());
//        }
//    }

    @GetMapping("/by-course")
    public List<ModuleDTO> getModulesByCourseTitle(@RequestParam("title") String courseTitle) {
        List<Module> modules = mS.findModulesByCourseTitle(courseTitle);

        if (modules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron modulos para el curso especificado");
        }

        // Convertir la lista de sesiones a SessionDTO
        ModelMapper modelMapper = new ModelMapper();
        return modules.stream().map(module -> {
            ModuleDTO dto = modelMapper.map(module, ModuleDTO.class);

            return dto;
        }).collect(Collectors.toList());
    }
}
