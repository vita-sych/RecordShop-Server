package org.vita.data;

import org.vita.models.ShoppingCart;
import org.vita.models.ShoppingCartItem;

import java.util.Map;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);
    Map<Integer, ShoppingCartItem> getAllItems(int userId);
    void addProduct(int userId, int productId);
    boolean updateProductQuantity(int userId, int productId, int quantity);
    boolean removeProduct(int userId, int productId);
    void delete(int userId);
}
