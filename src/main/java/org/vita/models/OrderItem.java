package org.vita.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private BigDecimal price;
    private int quantity;
    private BigDecimal discountPercent = new BigDecimal(0);

    public OrderItem() {}
}
