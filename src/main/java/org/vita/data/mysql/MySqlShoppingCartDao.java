package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.data.ProductDao;
import org.vita.data.ShoppingCartDao;
import org.vita.models.Product;
import org.vita.models.ShoppingCart;
import org.vita.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private final ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                Product product = productDao.getById(rs.getInt("product_id"));
                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));

                cart.getItems().put(item.getProductId(), item);
            }
            return cart;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading shopping cart", e);
        }
    }

    @Override
    public Map<Integer, ShoppingCartItem> getAllItems(int userId) {
        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?")) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(productDao.getById(rs.getInt("product_id")));
                item.setQuantity(rs.getInt("quantity"));

                items.put(item.getProductId(), item);
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading cart items", e);
        }
    }


    @Override
    public void addProduct(int userId, int product_id) {
        try (Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, product_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                try (PreparedStatement insertPs = connection.prepareStatement("UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?")) {
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, product_id);

                    insertPs.executeUpdate();
                }
            } else {
                try (PreparedStatement updatePs = connection.prepareStatement("INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)")) {
                    updatePs.setInt(1, userId);
                    updatePs.setInt(2, product_id);

                    updatePs.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding a product to the shopping cart", e);
        }
    }

    @Override
    public boolean updateProductQuantity(int user_id, int product_id, int quantity) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?")) {
            ps.setInt(1, quantity);
            ps.setInt(2, user_id);
            ps.setInt(3, product_id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating cart item", e);
        }
    }

    @Override
    public boolean removeProduct(int userId, int productId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating cart item", e);
        }
    }

    @Override
    public void delete(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM shopping_cart WHERE user_id = ?")) {
            statement.setInt(1, userId);

            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
