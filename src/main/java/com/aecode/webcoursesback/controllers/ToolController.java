package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.ToolDTO;
import com.aecode.webcoursesback.entities.Tool;
import com.aecode.webcoursesback.services.IToolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
@RequestMapping("/tool")
public class ToolController {
    @Autowired
    private IToolService tS;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(@RequestPart(value = "file", required = false) MultipartFile imagen,
            @RequestPart(value = "data", required = false) String dtoJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ToolDTO dto = objectMapper.readValue(dtoJson, ToolDTO.class);

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            Tool tool = modelMapper.map(dto, Tool.class);

            // Guardar la herramienta sin la imagen para obtener el ID generado
            tS.insert(tool);

            // Crear directorio único para el usuario si no existe
            String userUploadDir = uploadDir + File.separator + "tool" + File.separator + tool.getToolId();
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            String originalFilename = null;

            // Manejo del archivo de imagen
            if (imagen != null && !imagen.isEmpty()) {
                originalFilename = imagen.getOriginalFilename();
                byte[] bytes = imagen.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);

                // Asignar la ruta de la imagen a la herramienta
                tool.setPicture("/uploads/tool/" + tool.getToolId() + "/" + originalFilename);
                tS.insert(tool); // Actualizar con la imagen
            }

            return ResponseEntity.ok("Herramienta guardada correctamente con ID: " + tool.getToolId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al insertar el objeto en la base de datos: " + e.getMessage());
        }
    }

    @GetMapping
    public List<ToolDTO> list() {
        return tS.list().stream().map(tool -> {
            ModelMapper modelMapper = new ModelMapper();
            ToolDTO dto = modelMapper.map(tool, ToolDTO.class);

            return dto;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        tS.delete(id);
    }

    @GetMapping("/{id}")
    public ToolDTO listById(@PathVariable("id") Integer id) {
        ModelMapper modelMapper = new ModelMapper();

        // Obtener la herramienta por id
        Tool tool = tS.listId(id);

        // Verificar si la herramienta existe
        if (tool == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool not found");
        }

        // Mapear la entidad Tool al DTO ToolDTO
        ToolDTO dto = modelMapper.map(tool, ToolDTO.class);
        return dto;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestPart(value = "file", required = false) MultipartFile imagen,
            @RequestPart(value = "data", required = false) String dtoJson) {
        try {
            // Verificar si la herramienta existe
            Tool existingTool = tS.listId(id);
            if (existingTool == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Herramienta no encontrada");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            ToolDTO dto = objectMapper.readValue(dtoJson, ToolDTO.class);

            // Crear directorio único para la herramienta si no existe
            String toolUploadDir = uploadDir + File.separator + "tool" + File.separator + id;
            Path toolUploadPath = Paths.get(toolUploadDir);
            if (!Files.exists(toolUploadPath)) {
                Files.createDirectories(toolUploadPath);
            }

            String originalFilename = null;
            // Manejo del archivo de imagen
            if (imagen != null && !imagen.isEmpty()) {
                originalFilename = imagen.getOriginalFilename();
                byte[] bytes = imagen.getBytes();
                Path path = toolUploadPath.resolve(originalFilename);
                Files.write(path, bytes);

                // Actualizar la ruta de la imagen
                existingTool.setPicture("/uploads/tool/" + id + "/" + originalFilename);
            }

            // Actualizar los campos de la herramienta
            if (dto.getName() != null && !dto.getName().isEmpty()) {
                existingTool.setName(dto.getName());
            }
            // Guardar los cambios en la base de datos
            tS.insert(existingTool);

            return ResponseEntity.ok("Herramienta actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el objeto en la base de datos: " + e.getMessage());
        }
    }

}
