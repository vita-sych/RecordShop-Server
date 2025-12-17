package org.vita.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.vita.data.CategoryDao;
import org.vita.data.ProductDao;
import org.vita.models.Category;
import org.vita.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests
@RestController
@CrossOrigin(origins = "http://localhost:52330", allowCredentials = "true")
public class CategoriesController {
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action
    @GetMapping("/categories")
    public List<Category> getAll() {
        try {
            return categoryDao.getAllCategories();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add the appropriate annotation for a get action
    @GetMapping("/categories/{id}")
    public Category getById(@PathVariable int id) {
        try {
           return categoryDao.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("/categories/{id}/products")
    public List<Product> getProductsById(@PathVariable int id) {
        try {
            return productDao.listByCategoryId(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... our bad."
            );
        }
    }

    // add annotation to call this method for a POST action
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        try {
            return categoryDao.create(category);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... our bad."
            );
        }
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {
        try {
            Category existingCategory = categoryDao.getById(id);

            if (existingCategory == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                );
            }

            category.setCategoryId(id);

            categoryDao.update(id, category);

            return categoryDao.getById(id);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... our bad."
            );
        }
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id) {
        try {
            Category existing = categoryDao.getById(id);

            if (existing == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                );
            }

            categoryDao.delete(id);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... our bad."
            );
        }
    }
}
