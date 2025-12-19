package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.data.OrderDao;
import org.vita.models.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order create(Order order) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                     INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                     VALUES (?, ?, ?, ?, ?, ?, ?)
                     """, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getUserId());
            ps.setString(2, LocalDate.now().toString());
            ps.setString(3, order.getAddress());
            ps.setString(4, order.getCity());
            ps.setString(5, order.getState());
            ps.setString(6, order.getZip());
            ps.setInt(7, order.getShipping_amount());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setOrderId(rs.getInt(1));
                }
            }

            return order;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> getByUserId(int userId) {
        List<Order> orders = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM orders WHERE user_id = ? ORDER BY date DESC")) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setDate(LocalDate.parse(rs.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    order.setAddress(rs.getString("address"));
                    order.setCity(rs.getString("city"));
                    order.setState(rs.getString("state"));
                    order.setZip(rs.getString("zip"));
                    order.setShipping_amount(rs.getInt("shipping_amount"));

                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }

}
