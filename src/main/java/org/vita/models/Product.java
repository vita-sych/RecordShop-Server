package org.vita.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class Product
{
    private int productId;
    private String name;
    private BigDecimal price;
    private int categoryId;
    private String description;
    private String subCategory;
    private int stock;
    private boolean isFeatured;
    private String imageUrl;

    public Product()
    {
    }

}
