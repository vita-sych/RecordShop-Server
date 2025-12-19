package org.vita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vita.data.OrderDao;
import org.vita.data.OrderItemDao;
import org.vita.data.ProfileDao;
import org.vita.data.ShoppingCartDao;
import org.vita.data.UserDao;
import org.vita.models.Order;
import org.vita.models.OrderItem;
import org.vita.models.Profile;
import org.vita.models.ShoppingCartItem;
import org.vita.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class OrdersService {

    private final ShoppingCartDao shoppingCartDao;
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final OrderItemDao orderItemDao;
    private final ProfileDao profileDao;

    @Autowired
    public OrdersService(
            ShoppingCartDao shoppingCartDao,
            OrderDao orderDao,
            UserDao userDao,
            OrderItemDao orderItemDao,
            ProfileDao profileDao
    ) {
        this.shoppingCartDao = shoppingCartDao;
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.orderItemDao = orderItemDao;
        this.profileDao = profileDao;
    }

    public List<Order> getOrdersByUser(String username) {
        User user = userDao.getByUserName(username);
        int userId = user.getId();

        List<Order> orders = orderDao.getByUserId(userId);

        for (Order order : orders) {
            List<OrderItem> items = orderItemDao.getByOrderId(order.getOrderId());
            order.setItems(items);

            double total = items.stream()
                    .mapToDouble(i -> i.getPrice().intValue() * i.getQuantity())
                    .sum();
            order.setTotal(total);
        }

        return orders;
    }

    public Order addOrder(String username) {
        User user = userDao.getByUserName(username);

        Map<Integer, ShoppingCartItem> cartItems = shoppingCartDao.getAllItems(user.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty");
        }

        Profile profile = profileDao.getByUserId(user.getId());

        Order order = new Order();
        order.setUserId(user.getId());
        order.setDate(LocalDate.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());

        int totalQuantity = cartItems.values().stream()
                .mapToInt(ShoppingCartItem::getQuantity)
                .sum();
        order.setShipping_amount(totalQuantity);

        Order createdOrder = orderDao.create(order);

        for (ShoppingCartItem item : cartItems.values()) {
            OrderItem lineItem = new OrderItem();
            lineItem.setOrderId(createdOrder.getOrderId());
            lineItem.setProductId(item.getProduct().getProductId());
            lineItem.setQuantity(item.getQuantity());
            lineItem.setPrice(item.getProduct().getPrice());

            orderItemDao.create(lineItem);
        }

        shoppingCartDao.delete(user.getId());

        return createdOrder;
    }
}