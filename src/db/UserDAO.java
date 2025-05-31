package db;

import model.User;
import model.UserRole;

import java.util.List;

public interface UserDAO{

    QueryResult<User> search();
    void insert(String username, String password, UserRole role, String nome, String cognome);

    User update(String table, String update_fields, String condition, Object... params);

    List<User> delete(String condition, Object... params);

}
