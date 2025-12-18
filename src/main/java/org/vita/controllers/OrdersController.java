package org.vita.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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
@CrossOrigin(origins = "http://localhost:52330", allowCredentials = "true")
@PreAuthorize("permitAll()")
public class OrdersController {
    private final ShoppingCartDao shoppingCartDao;
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final OrderItemDao orderItemDao;
    private final ProfileDao profileDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public OrdersController(ShoppingCartDao shoppingCartDao, OrderDao orderDao, UserDao userDao, OrderItemDao orderItemDao, ProfileDao profileDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.orderItemDao = orderItemDao;
        this.profileDao = profileDao;
    }

    @PostMapping("/orders")
    public Order addOrder(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            Map<Integer, ShoppingCartItem> cartItems =
                    shoppingCartDao.getAllItems(user.getId());

            if (cartItems.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Shopping cart is empty"
                );
            }

            Order order = new Order();
            order.setUserId(user.getId());
            order.setDate(LocalDate.now());

            Profile profile = profileDao.getByUserId(user.getId());
            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());
            order.setShipping_amount(cartItems.values()
                    .stream()
                    .mapToInt(ShoppingCartItem::getQuantity)
                    .sum());

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
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
