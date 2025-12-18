package org.vita.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
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

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@RestController
public class OrdersController {

    private final ShoppingCartDao shoppingCartDao;
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final OrderItemDao orderItemDao;
    private final ProfileDao profileDao;

    @Autowired
    public OrdersController(
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

    // POST /orders
    @PostMapping("/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> addOrder(Principal principal) {
        try {
            User user = userDao.getByUserName(principal.getName());

            Map<Integer, ShoppingCartItem> cartItems =
                    shoppingCartDao.getAllItems(user.getId());

            if (cartItems.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }

            Profile profile = profileDao.getByUserId(user.getId());

            Order order = new Order();
            order.setUserId(user.getId());
            order.setDate(LocalDate.now());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());

            int totalQuantity = cartItems.values()
                    .stream()
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

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(createdOrder);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
