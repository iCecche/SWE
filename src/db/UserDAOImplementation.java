package db;

import class_logic.User;
import rowmapper.UserMapper;

import java.sql.ResultSet;
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

    // CRUD OPERATIONS - TODO test functions
    // TODO: implement DAO interface and override function with multiple definitions (searchAll, searchByID)
    public List<User> search(int id) {
        String sql = "SELECT * FROM \"USER\" WHERE id = ?";
        return db.execute_statement(sql, mapper, id);
    }

    public void insert(String username, String password) {
        String sql = "insert into \"USER\" values(?,?)";
        List<User> processed_rows = db.execute_statement(sql, mapper, username, password);
    }

    public void update(Integer id, String username, String password) {
        String sql = "update \"USER\" set username=?,password=? where id=?";
        List<User> processed_rows = db.execute_statement(sql, mapper, password, id);
    }

    public void delete(Integer id) {
        String sql = "delete from \"USER\" where id=?";
        List<User> processed_rows = db.execute_statement(sql, mapper, id);
    }
}
