package org.vita.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class Order {
    private int orderId;
    private int userId;
    private LocalDate date;
    private String address = "";
    private String city = "";
    private String state = "";
    private String zip = "";
    private int shipping_amount;
    private List<OrderItem> items;
    private double total;

    public Order() {}
}
