package db;

import model.User;
import model.UserRole;

import java.util.List;

public interface UserDAO{

    QueryResult<User> search();
    void insert(String username, String password, UserRole role, String nome, String cognome);

    List<User> update(int id, String username, String password);

    List<User> delete(String condition, Object... params);

}
