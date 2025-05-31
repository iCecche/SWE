package rowmapper;

import model.User;
import model.UserBuilder;
import model.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper extends RowMapper<User> {

    private final UserBuilder builder = UserBuilder.create();;

    @Override
    public User mapRow(ResultSet rs) throws SQLException {

        if(contains(rs, "id")) {
            int id = rs.getInt("id");
            builder.withId(id);
        }

        if(contains(rs, "username")) {
            String username = rs.getString("username");
            builder.withUsername(username);
        }

        if(contains(rs, "password")) {
            String password = rs.getString("password");
            builder.withPassword(password);
        }

        if(contains(rs, "role")) {
            UserRole role = UserRole.fromString(rs.getString("role"));
            builder.withRole(role);
        }

        if(contains(rs, "nome")) {
            String nome = rs.getString("nome");
            builder.withNome(nome);
        }

        if(contains(rs, "cognome")) {
            String cognome = rs.getString("cognome");
            builder.withCognome(cognome);
        }

        if(contains(rs, "indirizzo")) {
            String indirizzo = rs.getString("indirizzo");
            builder.withIndirizzo(indirizzo);
        }

        if(contains(rs, "cap")) {
            String cap = rs.getString("cap");
            builder.withCap(cap);
        }

        if(contains(rs, "provincia")) {
            String provincia = rs.getString("provincia");
            builder.withProvincia(provincia);
        }

        if(contains(rs, "stato")) {
            String stato = rs.getString("stato");
            builder.withStato(stato);
        }

        return builder.build();
    }
}
