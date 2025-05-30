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
        String sql = "SELECT * FROM USERS";
        return db.execute_query(sql, mapper).getResults();
    }

    public List<User> search(String condition, Object... params) {
        String sql = "SELECT * FROM USERS " +
                condition;
        return db.execute_query(sql, mapper, params).getResults();
    }

    // public method for user interface
    public List<User> searchAll() {
        return search();
    }

    public List<User> searchById(int id) {
        return search("WHERE id = ?", id);
    }

    public List<User> searchByUsername(String username) {
        username = username.toLowerCase();
        return search("WHERE username = ?", username);
    }

    public List<User> searchUsersInfo() {
        String condition = "LEFT JOIN USER_INFO ON USERS.id = USER_INFO.ID";
        return search(condition);
    }

    public List<User> searchUserInfoById(int id) {
        String condition = "LEFT JOIN USER_INFO ON USERS.id = USER_INFO.ID where USERS.ID = ?";
        return search(condition, id);
    }

    @Override
    public void insert(String username, String password) {
        String sql = "INSERT INTO USERS (username, password) VALUES(?,?)";
        QueryResult<User> processed_rows = db.execute_query(sql, mapper, username, password);
    }

    // TODO: scegliere come implementare inserimento custom dei dati (inserisco solo alcuni campi opzionali - telefono, email, ecc. - dinamicamente) -> uso un builder design pattern?
    public void insert(String username, String password, String condition) {
        //String sql = "INSERT INTO USER (username, password) VALUES(?,?)";
        //List<User> processed_rows = db.execute_statement(sql, mapper);
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
