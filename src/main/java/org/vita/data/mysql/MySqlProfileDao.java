package org.vita.data.mysql;

import org.springframework.stereotype.Component;
import org.vita.models.Profile;
import org.vita.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
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
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int id) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            System.out.println(rs);

            if(rs.next()) {

                Profile profile = Profile.builder()
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

                System.out.println(profile);

                return profile;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
