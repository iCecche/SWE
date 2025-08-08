package db;

import model.User;
import model.enums.UserRole;

import java.util.List;

public interface UserDAO{

    // Search
    List<User> searchAll();
    User searchById(int id);
    User searchByUsername(String username);
    List<User> searchUsersInfo();
    User searchUserInfoById(int id);

    // Insert
    Long newUser(String username, String password, UserRole role, String nome, String cognome);

    // Update
    void UpdateCredentials(int id, String username, String password);
    void UpdateRole(int id, UserRole role);
    void UpdateUserInfo(int id, String nome, String cognome, String indirizzo, String cap, String provincia, String stato);

    // Delete
    void deleteUser(int id);

}
