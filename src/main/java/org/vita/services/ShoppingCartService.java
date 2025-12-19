package org.vita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vita.data.ShoppingCartDao;
import org.vita.data.UserDao;
import org.vita.models.ShoppingCart;
import org.vita.models.User;

@Service
public class ShoppingCartService {

    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingCartService(ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    private User getUser(String username) {
        return userDao.getByUserName(username);
    }

    public ShoppingCart getCart(String username) {
        User user = getUser(username);
        return shoppingCartDao.getByUserId(user.getId());
    }

    public ShoppingCart addProduct(String username, int productId) {
        User user = getUser(username);
        shoppingCartDao.addProduct(user.getId(), productId);
        return shoppingCartDao.getByUserId(user.getId());
    }

    public ShoppingCart updateProductQuantity(String username, int productId, int quantity) {
        User user = getUser(username);
        boolean updated = shoppingCartDao.updateProductQuantity(user.getId(), productId, quantity);
        if (!updated) return null;
        return shoppingCartDao.getByUserId(user.getId());
    }

    public ShoppingCart removeProduct(String username, int productId) {
        User user = getUser(username);
        boolean removed = shoppingCartDao.removeProduct(user.getId(), productId);
        if (!removed) return null;
        return shoppingCartDao.getByUserId(user.getId());
    }

    public boolean clearCart(String username) {
        User user = getUser(username);
        ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
        if (cart == null) return false;
        shoppingCartDao.delete(user.getId());
        return true;
    }
}