package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseCartDTO;
import com.aecode.webcoursesback.services.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    IShoppingCartService scS;


    @GetMapping("/{email}")
    public ResponseEntity<List<CourseCartDTO>> getCart(@PathVariable String email) {
        List<CourseCartDTO> cart = scS.getCartByUser(email);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{email}/modules/{moduleId}")
    public ResponseEntity<Void> addModule(@PathVariable String email, @PathVariable Long moduleId) {
        scS.addModuleToCart(email, moduleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{email}/modules/{moduleId}")
    public ResponseEntity<Void> updateModuleSelection(@PathVariable String email,
                                                      @PathVariable Long moduleId,
                                                      @RequestParam boolean selected) {
        scS.updateModuleSelection(email, moduleId, selected);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cartitem/{cartId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartId) {
        scS.removeCartItemById(cartId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para eliminar todos los módulos de un curso en el carrito de un usuario
    @DeleteMapping("/{email}/course/{courseId}")
    public ResponseEntity<Void> removeCourseFromCart(@PathVariable String email, @PathVariable Long courseId) {
        scS.removeAllModulesFromCourse(email, courseId);
        return ResponseEntity.noContent().build();
    }
}