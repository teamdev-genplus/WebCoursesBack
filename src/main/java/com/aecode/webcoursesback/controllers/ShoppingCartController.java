package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.CourseCartDTO;
import com.aecode.webcoursesback.services.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    IShoppingCartService scS;


    @GetMapping("/{userId}")
    public ResponseEntity<CourseCartDTO> getCart(@PathVariable Long userId) {
        CourseCartDTO cart = scS.getCartByUser(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> addModule(@PathVariable Long userId, @PathVariable Long moduleId) {
        scS.addModuleToCart(userId, moduleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> updateModuleSelection(@PathVariable Long userId,
                                                      @PathVariable Long moduleId,
                                                      @RequestParam boolean selected) {
        scS.updateModuleSelection(userId, moduleId, selected);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/modules/{moduleId}")
    public ResponseEntity<Void> removeModule(@PathVariable Long userId, @PathVariable Long moduleId) {
        scS.removeModuleFromCart(userId, moduleId);
        return ResponseEntity.noContent().build();
    }
}