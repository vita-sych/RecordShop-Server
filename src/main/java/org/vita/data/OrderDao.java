package org.vita.data;

import org.vita.models.Order;

import java.util.List;

public interface OrderDao {
    Order create(Order order);
    List<Order> getByUserId(int userId);
}
