package org.vita.data;

import org.vita.models.OrderItem;

import java.util.List;

public interface OrderItemDao {
    void create(OrderItem orderItem);
    List<OrderItem> getByOrderId(int orderId);
}
