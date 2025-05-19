package rowmapper;

import model.User;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class UserMapper extends RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");

        if(contains(rs, "nome")) {
            String nome = rs.getString("nome");
            String cognome = rs.getString("cognome");
            String indirizzo = rs.getString("indirizzo");
            String cap = rs.getString("cap");
            String provincia = rs.getString("provincia");
            String stato = rs.getString("stato");
            return new User(id, username, password, nome, cognome, indirizzo, cap, provincia, stato);
        }

        return new User(id, username, password);
    }
}
