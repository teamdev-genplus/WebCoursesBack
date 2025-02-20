package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.SessionDTO;
import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.entities.Unit;
import com.aecode.webcoursesback.services.ISessionService;
import com.aecode.webcoursesback.services.IUnitService;
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
@RequestMapping("/session")
public class SessionController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private ISessionService cS;
    @Autowired
    private IUnitService uS;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(@RequestPart(value="file", required = false) MultipartFile imagen,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            SessionDTO dto= objectMapper.readValue(dtoJson, SessionDTO.class);

            String userUploadDir = uploadDir + File.separator + "session";
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
            Session classes = modelMapper.map(dto, Session.class);
            // Establecer la ruta del archivo en la entidad
            classes.setResourceDocument("/uploads/session/" +originalFilename);
            cS.insert(classes);

            return ResponseEntity.ok("Sesión guardado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el objeto en la base de datos: " + e.getMessage());
        }
    }


    @GetMapping("/search")
    public List<SessionDTO> searchByTitle(@RequestParam("title") String title) {
        return cS.findByTitle(title).stream().map(session -> {
            ModelMapper m = new ModelMapper();
            SessionDTO dto = m.map(session, SessionDTO.class);
            dto.setHtmlContent(cS.wrapInHtml(session.getDescription()));
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping
    public List<SessionDTO> list() {
        return cS.list().stream().map(session -> {
            ModelMapper m = new ModelMapper();
            SessionDTO dto = m.map(session, SessionDTO.class);
            dto.setHtmlContent(cS.wrapInHtml(session.getDescription()));
            return dto;
        }).collect(Collectors.toList());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){cS.delete(id);}

    @GetMapping("/{id}")
    public SessionDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        Session session = cS.listId(id);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada");
        }
        SessionDTO dto = m.map(session, SessionDTO.class);
        dto.setHtmlContent(cS.wrapInHtml(session.getDescription())); // aqui se añade el HTML formateado
        return dto;
    }
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestPart(value = "file", required = false) MultipartFile document,
            @RequestPart(value = "data", required = false) String dtoJson) {

        try {
            // Buscar la sesión existente por ID
            Session existingSession = cS.listId(id);
            if (existingSession == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sesión no encontrada");
            }

            // Procesar datos enviados en JSON
            if (dtoJson != null && !dtoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                SessionDTO dto = objectMapper.readValue(dtoJson, SessionDTO.class);

                // Actualizar los campos según el contenido del JSON
                if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
                    existingSession.setTitle(dto.getTitle());
                }
                if (dto.getVideoUrl() != null && !dto.getVideoUrl().isEmpty()) {
                    existingSession.setVideoUrl(dto.getVideoUrl());
                }
                if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
                    existingSession.setDescription(dto.getDescription());
                }
                if (dto.getTaskName() != null && !dto.getTaskName().isEmpty()) {
                    existingSession.setTaskName(dto.getTaskName());
                }
                if (dto.getTaskUrl() != null && !dto.getTaskUrl().isEmpty()) {
                    existingSession.setTaskUrl(dto.getTaskUrl());
                }
                if (dto.getOrderNumber() > 0) {
                    existingSession.setOrderNumber(dto.getOrderNumber());
                }
                if (dto.getUnitId() > 0) {
                    Unit unit = uS.listId(dto.getUnitId());
                    if (unit == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unidad asociada no encontrada");
                    }
                    existingSession.setUnit(unit);
                }
            }

            // Procesar el archivo enviado
            if (document != null && !document.isEmpty()) {
                // Configurar ruta de almacenamiento del archivo
                String userUploadDir = uploadDir + File.separator + "session";
                Path userUploadPath = Paths.get(userUploadDir);

                // Crear el directorio si no existe
                if (!Files.exists(userUploadPath)) {
                    Files.createDirectories(userUploadPath);
                }

                // Guardar el archivo en el directorio
                String originalFilename = document.getOriginalFilename();
                byte[] bytes = document.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);

                // Actualizar la ruta del documento en la sesión
                existingSession.setResourceDocument("/uploads/session/" + originalFilename);
            }

            // Guardar la sesión actualizada
            cS.insert(existingSession);

            return ResponseEntity.ok("Sesión actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la sesión: " + e.getMessage());
        }
    }



    @GetMapping("/by-course")
    public List<SessionDTO> getSessionsByCourseTitle(@RequestParam("title") String courseTitle) {
        List<Session> sessions = cS.findSessionsByCourseTitle(courseTitle);

        if (sessions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron sesiones para el curso especificado");
        }

        // Convertir la lista de sesiones a SessionDTO
        ModelMapper modelMapper = new ModelMapper();
        return sessions.stream().map(session -> {
            SessionDTO dto = modelMapper.map(session, SessionDTO.class);
            // Agregar HTML formateado si es necesario
            dto.setHtmlContent(cS.wrapInHtml(session.getDescription()));
            return dto;
        }).collect(Collectors.toList());
    }
    
}
