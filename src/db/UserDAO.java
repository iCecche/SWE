package db;

import model.User;
import java.util.List;

public interface UserDAO{

    List<User> search();

    void insert(String username, String password);

    List<User> update(int id, String username, String password);

    List<User> delete(String condition, Object... params);

}
