package org.vita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vita.data.ProductDao;
import org.vita.models.Product;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductsService {

    private final ProductDao productDao;

    @Autowired
    public ProductsService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> searchProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                        String subCategory, String name, String order) {
        return productDao.search(categoryId, minPrice, maxPrice, subCategory, name, order);
    }

    public Product getProductById(int id) {
        return productDao.getById(id);
    }

    public Product addProduct(Product product) {
        return productDao.create(product);
    }

    public boolean updateProduct(int id, Product product) {
        Product existing = productDao.getById(id);
        if (existing == null) return false;

        productDao.update(id, product);
        return true;
    }

    public boolean deleteProduct(int id) {
        Product existing = productDao.getById(id);
        if (existing == null) return false;

        productDao.delete(id);
        return true;
    }
}