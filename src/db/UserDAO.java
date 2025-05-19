package db;

import model.User;
import java.util.List;

public interface UserDAO{

    abstract List<User> search();

    abstract void insert(String username, String password);

    abstract void update(int id, String username, String password);

    abstract void delete(int id);

}
