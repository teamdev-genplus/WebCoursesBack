package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserFavoriteDTO;
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
    @PostMapping("/{clerkId}/courses/{courseId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String clerkId, @PathVariable Long courseId) {
        userFavoriteService.addFavorite(clerkId, courseId);
        return ResponseEntity.ok().build();
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

}
