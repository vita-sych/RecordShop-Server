package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.data.CategoryDao;
import org.vita.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM categories");
             ResultSet rs = ps.executeQuery()
        ) {
            List<Category> categories = new ArrayList<>();

            while (rs.next()) {
                categories.add(Category.builder()
                        .categoryId(rs.getInt("category_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .build());
            }

            System.out.println(categories);

            return categories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category getById(int categoryId)
    {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM categories WHERE category_id = ?")
        ) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Category.builder()
                        .categoryId(rs.getInt("category_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category)
    {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                     INSERT INTO categories (category_id, name, description)
                     VALUES (?, ?, ?)
                     """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, category.getCategoryId());
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());

            ps.executeUpdate();

            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int categoryId, Category category) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE categories SET name = ?, description = ? WHERE category_id = ?")) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, categoryId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
