package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.data.OrderItemDao;
import org.vita.models.OrderItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
