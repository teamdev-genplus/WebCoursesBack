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


    @GetMapping("/{clerkId}")
    public ResponseEntity<List<CourseCartDTO>> getCart(@PathVariable String clerkId) {
        List<CourseCartDTO> cart = scS.getCartByUser(clerkId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{clerkId}/modules/{moduleId}")
    public ResponseEntity<Void> addModule(@PathVariable String clerkId, @PathVariable Long moduleId) {
        scS.addModuleToCart(clerkId, moduleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{clerkId}/modules/{moduleId}")
    public ResponseEntity<Void> updateModuleSelection(@PathVariable String clerkId,
                                                      @PathVariable Long moduleId,
                                                      @RequestParam boolean selected) {
        scS.updateModuleSelection(clerkId, moduleId, selected);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cartitem/{cartId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartId) {
        scS.removeCartItemById(cartId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para eliminar todos los módulos de un curso en el carrito de un usuario
    @DeleteMapping("/{clerkId}/course/{courseId}")
    public ResponseEntity<Void> removeCourseFromCart(@PathVariable String clerkId, @PathVariable Long courseId) {
        scS.removeAllModulesFromCourse(clerkId, courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{clerkId}/modules")
    public ResponseEntity<Void> addModulesToCart(@PathVariable String clerkId, @RequestBody List<Long> moduleIds) {
        scS.addModulesToCart(clerkId, moduleIds);
        return ResponseEntity.ok().build();
    }

}