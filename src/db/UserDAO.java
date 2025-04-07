package db;

import class_logic.User;
import java.util.List;

public interface UserDAO{

    abstract List<User> search(int id);

    abstract void insert(String username, String password);

    abstract void update(Integer id, String username, String password);

    abstract void delete(Integer id);

}
