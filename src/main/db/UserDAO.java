package main.db;

import main.model.User;
import main.model.enums.UserRole;

public interface UserDAO{

    QueryResult<User> search(String sql, Object... params);
    void insert(String username, String password, UserRole role, String nome, String cognome);
    QueryResult<User> update(String sql, Object... params);
    QueryResult<User> delete(String sql, Object... params);

}
