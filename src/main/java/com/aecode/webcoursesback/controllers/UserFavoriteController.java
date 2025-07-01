package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class UserFavoriteController {

    @Autowired
    private IUserFavoriteService userFavoriteService;

    // Agrega un curso a la lista de favoritos del usuario
    @PostMapping("/{userId}/courses/{courseId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long userId, @PathVariable Long courseId) {
        userFavoriteService.addFavorite(userId, courseId);
        return ResponseEntity.ok().build();
    }

    // Elimina un curso de la lista de favoritos del usuario
    @DeleteMapping("/{userId}/courses/{courseId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId, @PathVariable Long courseId) {
        userFavoriteService.removeFavorite(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
