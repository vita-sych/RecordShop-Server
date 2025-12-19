package org.vita.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vita.models.ShoppingCart;
import org.vita.models.ShoppingCartItem;
import org.vita.services.ShoppingCartService;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ResponseEntity<ShoppingCart> getCart(Principal principal) {
        try {
            ShoppingCart cart = shoppingCartService.getCart(principal.getName());

            if (cart == null) return ResponseEntity.notFound().build();

            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> addProduct(@PathVariable int productId, Principal principal) {
        try {
            ShoppingCart cart = shoppingCartService.addProduct(principal.getName(), productId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> updateProductQuantity(
            @PathVariable int productId,
            Principal principal,
            @RequestBody ShoppingCartItem item
    ) {
        if (item.getQuantity() <= 0) return ResponseEntity.badRequest().build();
        try {
            ShoppingCart cart = shoppingCartService.updateProductQuantity(principal.getName(), productId, item.getQuantity());

            if (cart == null) return ResponseEntity.notFound().build();

            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> removeProduct(@PathVariable int productId, Principal principal) {
        try {
            ShoppingCart cart = shoppingCartService.removeProduct(principal.getName(), productId);

            if (cart == null) return ResponseEntity.notFound().build();

            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        try {
            boolean cleared = shoppingCartService.clearCart(principal.getName());

            if (!cleared) return ResponseEntity.notFound().build();

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}