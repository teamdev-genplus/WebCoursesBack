package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserFavoriteDTO;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class UserFavoriteController {

    @Autowired
    private IUserFavoriteService userFavoriteService;

    // Agrega un curso a la lista de favoritos del usuario
    @PostMapping("/{clerkId}/courses/{courseId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String clerkId, @PathVariable Long courseId) {
        userFavoriteService.addFavorite(clerkId, courseId);
        return ResponseEntity.noContent().build();
    }

    // Elimina un curso de la lista de favoritos del usuario
    @DeleteMapping("/{clerkId}/courses/{courseId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String clerkId, @PathVariable Long courseId) {
        userFavoriteService.removeFavorite(clerkId, courseId);
        return ResponseEntity.noContent().build();
    }
    // Listar todos los favoritos
    @GetMapping("/all")
    public ResponseEntity<List<UserFavoriteDTO>> getAllFavorites() {
        return ResponseEntity.ok(userFavoriteService.getAllFavorites());
    }

    // Listar favoritos por clerkId
    @GetMapping("/{clerkId}")
    public ResponseEntity<List<UserFavoriteDTO>> getFavoritesByClerkId(@PathVariable String clerkId) {
        return ResponseEntity.ok(userFavoriteService.getFavoritesByClerkId(clerkId));
    }

    // ---- Manejo de errores legibles para el front ----
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<String> handleConstraint(DataIntegrityViolationException ex) {
        // por ejemplo, si intentan insertar duplicado pese al control
        return ResponseEntity.status(409).body("El curso ya está en favoritos.");
    }

}
