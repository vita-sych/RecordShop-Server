package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.data.OrderItemDao;
import org.vita.models.OrderItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlOrderItemDao extends MySqlDaoBase implements OrderItemDao {
    public MySqlOrderItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void create(OrderItem orderItem) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                     INSERT INTO order_line_items (order_line_item_id, order_id, product_id, sales_price, quantity, discount)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderItem.getOrderItemId());
            ps.setInt(2, orderItem.getOrderId());
            ps.setInt(3, orderItem.getProductId());
            ps.setBigDecimal(4, orderItem.getPrice());
            ps.setInt(5, orderItem.getQuantity());
            ps.setBigDecimal(6, orderItem.getDiscountPercent());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OrderItem> getByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();

        String sql = "SELECT * FROM order_line_items WHERE order_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_line_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setPrice(rs.getBigDecimal("sales_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDiscountPercent(rs.getBigDecimal("discount"));

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return items;
    }

}
