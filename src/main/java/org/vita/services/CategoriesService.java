package org.vita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vita.data.CategoryDao;
import org.vita.data.ProductDao;
import org.vita.models.Category;
import org.vita.models.Product;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    @Autowired
    public CategoriesService(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryDao.getById(id);
    }

    public List<Product> getProductsByCategoryId(int id) {
        return productDao.listByCategoryId(id);
    }

    public Category addCategory(Category category) {
        return categoryDao.create(category);
    }

    public Category updateCategory(int id, Category category) {
        Category existing = categoryDao.getById(id);
        if (existing == null) {
            return null;
        }
        category.setCategoryId(id);
        categoryDao.update(id, category);
        return categoryDao.getById(id);
    }

    public boolean deleteCategory(int id) {
        Category existing = categoryDao.getById(id);
        if (existing == null) {
            return false;
        }
        categoryDao.delete(id);
        return true;
    }
}
