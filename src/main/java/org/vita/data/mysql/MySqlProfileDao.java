package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.models.Profile;
import org.vita.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {
    public MySqlProfileDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                     INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM profiles WHERE user_id = ?")) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return Profile.builder()
                        .userId(id)
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .address(rs.getString("address"))
                        .city(rs.getString("city"))
                        .state(rs.getString("state"))
                        .zip(rs.getString("zip"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("There is no profile for the user", e);
        }
        return null;
    }

    @Override
    public Profile update(Profile profile) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                UPDATE profiles
                SET first_name = ?, last_name = ?, phone = ?, email = ?, address = ?, city = ?, state = ?, zip = ?
                WHERE user_id = ?
                """)) {
            ps.setString(1, profile.getFirstName());
            ps.setString(2, profile.getLastName());
            ps.setString(3, profile.getPhone());
            ps.setString(4, profile.getEmail());
            ps.setString(5, profile.getAddress());
            ps.setString(6, profile.getCity());
            ps.setString(7, profile.getState());
            ps.setString(8, profile.getZip());
            ps.setInt(9, profile.getUserId());

            ps.executeUpdate();

            return getByUserId(profile.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update profile", e);
        }
    }

}
