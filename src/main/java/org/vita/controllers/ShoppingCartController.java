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
import org.springframework.web.bind.annotation.RestController;
import org.vita.data.ShoppingCartDao;
import org.vita.data.UserDao;
import org.vita.models.ShoppingCart;
import org.vita.models.ShoppingCartItem;
import org.vita.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController {
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @GetMapping
    public ResponseEntity<ShoppingCart> getCart(Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());
            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());

            if (cart == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> addProduct(@PathVariable int productId, Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());
            shoppingCartDao.addProduct(user.getId(), productId);
            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
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
        if (item.getQuantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User user = userDao.getByUserName(principal.getName());
            boolean updated = shoppingCartDao.updateProductQuantity(user.getId(), productId, item.getQuantity());

            if (!updated) {
                return ResponseEntity.notFound().build();
            }

            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> removeProduct(@PathVariable int productId, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            boolean removed = shoppingCartDao.removeProduct(user.getId(), productId);

            if (!removed) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(shoppingCartDao.getByUserId(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());
            ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());

            if (cart == null) {
                return ResponseEntity.notFound().build();
            }

            shoppingCartDao.delete(user.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
