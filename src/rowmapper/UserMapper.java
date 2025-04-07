package rowmapper;

import class_logic.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper extends RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");

        return new User(id, username, password);
    }
}
