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
    // TODO: implement DAO interface and override function with multiple definitions (searchAll, searchByID)
    @Override
    public List<User> search() {
        String sql = "SELECT * FROM USER";
        return db.execute_statement(sql, mapper);
    }

    public List<User> search(int id) {
        String sql = "SELECT * FROM USER WHERE id = ?";
        return db.execute_statement(sql, mapper, id);
    }

    public List<User> searchUserInfo(int id) {
        String sql = "SELECT * FROM USER JOIN USER_INFO ON USER_INFO.id = USER.ID where USER.ID = ?";
        return db.execute_statement(sql, mapper, id);
    }

    @Override
    public void insert(String username, String password) {
        String sql = "INSERT INTO USER (username, password) VALUES(?,?)";
        List<User> processed_rows = db.execute_statement(sql, mapper, username, password);
        processed_rows.forEach(User::print);
    }

    @Override
    public void update(int id, String username, String password) {
        String sql = "update USER set username=?,password=? where id=?";
        List<User> processed_rows = db.execute_statement(sql, mapper, username, password, id);
        processed_rows.forEach(User::print);
    }

    @Override
    public void delete(int id) {
        String sql = "delete from USER where id=?";
        List<User> processed_rows = db.execute_statement(sql, mapper, id);
        processed_rows.forEach(User::print);
    }
}
