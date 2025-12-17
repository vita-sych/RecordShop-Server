package org.vita.data;

import org.vita.models.ShoppingCart;
import org.vita.models.ShoppingCartItem;

import java.util.Map;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);
    Map<Integer, ShoppingCartItem> getAllItems(int userId);
    void addProduct(int userId, int productId);
    boolean updateProductQuantity(int user_id, int product_id, int quantity);
    void delete(int userId);
}
