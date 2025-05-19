package db;

import model.User;
import rowmapper.UserMapper;

import java.sql.SQLException;
import java.util.List;

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
    public List<User> search() {
        String sql = "SELECT * FROM USER";
        return db.execute_statement(sql, mapper);
    }

    public List<User> search(String condition, Object... params) {
        String sql = "SELECT * FROM USER" +
                condition;
        return db.execute_statement(sql, mapper, params);
    }

    // public method for user interface
    public List<User> searchAll() {
        return search();
    }

    public List<User> searchById(int id) {
        return search("WHERE id = ?", id);
    }

    public List<User> searchUserInfo(int id) {
        String condition = "JOIN USER_INFO ON USER_INFO.id = USER.ID where USER.ID = ?";
        return search(condition, id);
    }

    @Override
    public void insert(String username, String password) {
        String sql = "INSERT INTO USER (username, password) VALUES(?,?)";
        List<User> processed_rows = db.execute_statement(sql, mapper, username, password);
        processed_rows.forEach(User::print);
    }

    // TODO: scegliere come implementare inserimento custom dei dati (inserisco solo alcuni campi opzionali - telefono, email, ecc. - dinamicamente) -> uso un builder design pattern?
    public void insert(String username, String password, String condition) {
        //String sql = "INSERT INTO USER (username, password) VALUES(?,?)";
        //List<User> processed_rows = db.execute_statement(sql, mapper);
    }

    @Override
    public List<User> update(int id, String username, String password) {
        String sql = "update USER set username=?,password=? where id=?";
        return db.execute_statement(sql, mapper, username, password, id);
    }

    public List<User> update(String update_fields, String condition, Object... params) {
        String sql = "UPDATE USER SET " + update_fields +
                "FROM USER " +
                "JOIN USER_INFO ON USER_INFO.ID = USER.ID " +
                " WHERE " + condition;
        return db.execute_statement(sql, mapper, params);
    }

    public void UpdateCredentials(int id, String username, String password) {
        String condition = "id = ?";
        String update_fields = "username = ?, password = ?";
        List<User> processed_rows = update(update_fields, condition, username, password, id);

        processed_rows.forEach(User::print); // CLI result view
    }

    public void UpdateUserInfo(int id, String name, String surname, String address, String cap, String province, String email, String phone) {
        String condition = "id = ?";
        String update_fields = "name = ?, surname = ?, address = ?, cap = ?, province = ?, email = ?, phone = ?";
        List<User> processed_rows = update(update_fields, condition, name, surname, address, cap, province, email, phone, id);
    }

    @Override
    public List<User> delete(String condition, Object... params) {
        String sql = "delete from USER where " + condition;
        return db.execute_statement(sql, mapper, params);
    }

    public void deleteUser(int id) {
        String condition = "id = ?";
        List<User> processed_rows = delete(condition, id);
        processed_rows.forEach(User::print);
    }
}
