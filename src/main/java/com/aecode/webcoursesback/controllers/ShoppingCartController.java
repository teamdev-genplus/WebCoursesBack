package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ShoppingCartDTO;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.entities.ShoppingCart;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.ISecondCourseService;
import com.aecode.webcoursesback.services.IShoppingCartService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    IShoppingCartService scS;
    @Autowired
    private IUserProfileService pS;
    @Autowired
    private ISecondCourseService sS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody ShoppingCartDTO dto) {

        // Cargar manualmente las entidades UserProfile y Session
        UserProfile user = pS.listId(dto.getUserId());
        SecondaryCourses secondaryCourses = sS.listId(dto.getSeccourseId());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        if (secondaryCourses == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso secundario no encontrado");
        }

        // Mapear el DTO a la entidad
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserProfile(user); // Asignar el UserProfile
        shoppingCart.setSecondaryCourse(secondaryCourses); // Asignar el SecondaryCourse

        // Guardar en la base de datos
        scS.insert(shoppingCart);

        return ResponseEntity.ok("Curso Secundario del usuario guardado correctamente");
    }

    @GetMapping
    public List<ShoppingCartDTO> list() {
        return scS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, ShoppingCartDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        scS.delete(id);
    }
}