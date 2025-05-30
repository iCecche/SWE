package db;

import model.User;
import model.UserRole;
import rowmapper.UserMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDAOImplementation implements UserDAO{
    private final DBManager db;
    private final UserMapper mapper;

    public UserDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new UserMapper();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // CRUD OPERATIONS
    // TODO: choose interface or abstract class, implemented search methods must be private (searchAll... only accessible methods)
    @Override
    public QueryResult<User> search() {
        String sql = "SELECT * FROM USERS";
        return db.execute_query(sql, mapper);
    }

    public QueryResult<User> search(String condition, Object... params) {
        String sql = "SELECT * FROM USERS " +
                condition;
        return db.execute_query(sql, mapper, params);
    }

    // public method for user interface
    public List<User> searchAll() {
        return search().getResults();
    }

    public User searchById(int id) {
        return search("WHERE id = ?", id).getSingleResult().orElse(null);
    }

    public User searchByUsername(String username) {
        username = username.toLowerCase();
        return search("WHERE username = ?", username).getSingleResult().orElse(null);
    }

    public List<User> searchUsersInfo() {
        String condition = "LEFT JOIN USER_INFO ON USERS.id = USER_INFO.ID";
        return search(condition).getResults();
    }

    public User searchUserInfoById(int id) {
        String condition = "LEFT JOIN USER_INFO ON USERS.id = USER_INFO.ID where USERS.ID = ?";
        return search(condition, id).getSingleResult().orElse(null);
    }

    @Override
    public void insert(String username, String password, UserRole role, String nome, String cognome) {
        db.execute_transaction(() -> {
            Long userId = insertUser(username, password, role);
            insertUserInfo(userId, nome, cognome);
            return null;
        });
    }

    private Long insertUser(String username, String password, UserRole role) {
        String sql1 = "INSERT INTO USERS (username, password, role) VALUES (?, ?, ?::user_role)";
        QueryResult<User> result = db.execute_query(sql1, mapper, username, password, role.toString());

        Long userId = result.getGeneratedKey().orElse(null);

        if(userId == null) {
            throw new RuntimeException("Error inserting user!");
        }

        return userId;
    }

    private void insertUserInfo(Long userId, String nome, String cognome) {
        String sql2 = "INSERT INTO USER_INFO (id, nome, cognome) VALUES (?, ?, ?)";
        QueryResult<User> result = db.execute_query(sql2, mapper, userId, nome, cognome);

        if(result.getGeneratedKey().isEmpty()) {
            throw new RuntimeException("Error inserting user info!");
        }
    }

    @Override
    public List<User> update(int id, String username, String password) {
        String sql = "update USERS set username=?,password=? where id=?";
        return db.execute_query(sql, mapper, username, password, id).getResults();
    }

    public List<User> update(String update_fields, String condition, Object... params) {
        String sql = "UPDATE USER_INFO SET " + update_fields + " WHERE " + condition;
        return db.execute_query(sql, mapper, params).getResults();
    }

    public void UpdateCredentials(int id, String username, String password) {
        String condition = "id = ?";
        String update_fields = "username = ?, password = ?";
        List<User> processed_rows = update(update_fields, condition, username, password, id);

        processed_rows.forEach(User::print); // CLI result view
    }

    public void UpdateRole(int id, UserRole role) {
        String condition = "id = ?";
        String update_fields = "role = ?::user_role";
        update(update_fields, condition, role.toString(), id);
    }

    public void UpdateUserInfo(int id, String nome, String cognome, String indirizzo, String cap, String provincia, String stato) {
        String condition = "id = ?";
        String update_fields = "nome = ?, cognome = ?, indirizzo = ?, cap = ?, provincia = ?, stato = ? ";
        update(update_fields, condition, nome, cognome, indirizzo, cap, provincia, stato, id);
    }

    @Override
    public List<User> delete(String condition, Object... params) {
        String sql = "delete from USERS where " + condition;
        return db.execute_query(sql, mapper, params).getResults();
    }

    public void deleteUser(int id) {
        String condition = "id = ?";
        List<User> processed_rows = delete(condition, id);
        processed_rows.forEach(User::print);
    }
}
