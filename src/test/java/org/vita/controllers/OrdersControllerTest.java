package org.vita.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.vita.models.Order;
import org.vita.services.OrdersService;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdersControllerTest {

    @Mock
    private OrdersService ordersService;

    @InjectMocks
    private OrdersController controller;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrdersSuccess() {
        when(principal.getName()).thenReturn("john");

        Order order1 = new Order();
        order1.setTotal(20.0);

        Order order2 = new Order();
        order2.setTotal(5.0);

        when(ordersService.getOrdersByUser("john"))
                .thenReturn(List.of(order1, order2));

        ResponseEntity<List<Order>> response =
                controller.getOrders(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(ordersService).getOrdersByUser("john");
    }

    @Test
    void testGetOrdersException() {
        when(principal.getName()).thenReturn("john");
        when(ordersService.getOrdersByUser("john"))
                .thenThrow(new RuntimeException());

        ResponseEntity<List<Order>> response =
                controller.getOrders(principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testAddOrderSuccess() {
        when(principal.getName()).thenReturn("john");

        Order createdOrder = new Order();
        createdOrder.setOrderId(200);

        when(ordersService.addOrder("john"))
                .thenReturn(createdOrder);

        ResponseEntity<Order> response =
                controller.addOrder(principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdOrder, response.getBody());

        verify(ordersService).addOrder("john");
    }

    @Test
    void testAddOrderException() {
        when(principal.getName()).thenReturn("john");
        when(ordersService.addOrder("john"))
                .thenThrow(new RuntimeException());

        ResponseEntity<Order> response =
                controller.addOrder(principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
