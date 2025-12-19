package org.vita.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.vita.models.Category;
import org.vita.models.Product;
import org.vita.services.CategoriesService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriesControllerTest {

    @Mock
    private CategoriesService categoriesService;

    @InjectMocks
    private CategoriesController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(
                new Category(1, "Music", "Description 1"),
                new Category(2, "Books", "Description 2")
        );

        when(categoriesService.getAllCategories()).thenReturn(categories);

        ResponseEntity<List<Category>> response = controller.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(categoriesService).getAllCategories();
    }

    @Test
    void testGetByIdFound() {
        Category category = new Category(1, "Music", "Description 1");

        when(categoriesService.getCategoryById(1)).thenReturn(category);

        ResponseEntity<Category> response = controller.getById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(categoriesService).getCategoryById(1);
    }

    @Test
    void testGetByIdNotFound() {
        when(categoriesService.getCategoryById(1)).thenReturn(null);

        ResponseEntity<Category> response = controller.getById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoriesService).getCategoryById(1);
    }

    @Test
    void testGetProductsById() {
        List<Product> products = Arrays.asList(
                new Product(1, "Guitar", new BigDecimal(20), 1,
                        "Description", "SubCategory 1", 10, false, ""),
                new Product(2, "Piano", new BigDecimal(20), 1,
                        "Description", "SubCategory 1", 10, false, "")
        );

        when(categoriesService.getProductsByCategoryId(1)).thenReturn(products);

        ResponseEntity<List<Product>> response = controller.getProductsById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(categoriesService).getProductsByCategoryId(1);
    }

    @Test
    void testAddCategory() {
        Category input = new Category(0, "Electronics", "Description");
        Category created = new Category(1, "Electronics", "Description");

        when(categoriesService.addCategory(input)).thenReturn(created);

        ResponseEntity<Category> response = controller.addCategory(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
        verify(categoriesService).addCategory(input);
    }

    @Test
    void testUpdateCategoryFound() {
        Category updated = new Category(1, "Instruments", "Description");

        when(categoriesService.updateCategory(1, updated)).thenReturn(updated);

        ResponseEntity<Category> response = controller.updateCategory(1, updated);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
        verify(categoriesService).updateCategory(1, updated);
    }

    @Test
    void testUpdateCategoryNotFound() {
        when(categoriesService.updateCategory(eq(1), any()))
                .thenReturn(null);

        ResponseEntity<Category> response =
                controller.updateCategory(1, new Category());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoriesService).updateCategory(eq(1), any());
    }

    @Test
    void testDeleteCategoryFound() {
        when(categoriesService.deleteCategory(1)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteCategory(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoriesService).deleteCategory(1);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoriesService.deleteCategory(1)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteCategory(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoriesService).deleteCategory(1);
    }
}
