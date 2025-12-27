package services;

import model.User;
import model.enums.UserRole;
import orm.UserDAO;
import orm.UserDAOImplementation;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImplementation();
    }

    public User getUserById(int id) {
        return userDAO.searchById(id);
    }

    public User getUserByUsername(String username) {
        return userDAO.searchByUsername(username);
    }

    public List<User> searchUsersInfo() {
        return userDAO.searchUsersInfo();
    }

    public User searchUserInfoById(int id) {
        return userDAO.searchUserInfoById(id);
    }

    public void createUser(String username, String password, UserRole role, String nome, String cognome) {
        userDAO.newUser(username, password, role, nome, cognome);
    }

    public void updateUserInfo(int id, String nome, String cognome, String indirizzo, String cap, String provincia, String stato) {
        userDAO.UpdateUserInfo(id, nome, cognome, indirizzo, cap, provincia, stato);
    }

    public void updateRole(int id, UserRole newRole) {
        userDAO.UpdateRole(id, newRole);
    }

    public void deleteUser(int id) {
        userDAO.deleteUser(id);
    }
}
