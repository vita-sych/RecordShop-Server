package org.vita.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.vita.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MySqlCategoryDaoTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private MySqlCategoryDao categoryDao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        categoryDao = new MySqlCategoryDao(dataSource);
    }

    @Test
    void testGetAllCategories() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
                .thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Rock");
        when(resultSet.getString("description")).thenReturn("Rock music");

        List<Category> categories = categoryDao.getAllCategories();

        assertEquals(1, categories.size());
        assertEquals("Rock", categories.get(0).getName());

        verify(preparedStatement).executeQuery();
    }

    @Test
    void testGetByIdFound() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
                .thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Jazz");
        when(resultSet.getString("description")).thenReturn("Jazz music");

        Category category = categoryDao.getById(1);

        assertNotNull(category);
        assertEquals("Jazz", category.getName());
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeQuery())
                .thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        Category category = categoryDao.getById(99);

        assertNull(category);
    }

    @Test
    void testCreateCategory() throws Exception {
        when(connection.prepareStatement(anyString(), anyInt()))
                .thenReturn(preparedStatement);

        Category category = Category.builder()
                .categoryId(1)
                .name("Pop")
                .description("Pop music")
                .build();

        Category result = categoryDao.create(category);

        assertEquals(category, result);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdateCategory() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);

        Category category = Category.builder()
                .name("Updated")
                .description("Updated description")
                .build();

        assertDoesNotThrow(() ->
                categoryDao.update(1, category)
        );

        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDeleteCategory() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(preparedStatement);

        assertDoesNotThrow(() ->
                categoryDao.delete(1)
        );

        verify(preparedStatement).executeUpdate();
    }
}
