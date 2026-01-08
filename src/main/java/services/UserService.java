package services;

import model.User;
import model.enums.UserRole;
import model.exceptions.UserServiceException;
import orm.UserDAO;
import orm.UserDAOImplementation;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserService() {
        this.userDAO = new UserDAOImplementation();
    }

    public User getUserById(int id) {
        return userDAO.searchById(id);
    }

    public User getUserByUsername(String username) {
        return userDAO.searchByUsername(username);
    }

    public List<User> getUsersInfo() {
        return userDAO.searchUsersInfo();
    }

    public User getUserInfoById(int id) {

        User user = userDAO.searchUserInfoById(id);
        if (user == null) {
            throw new UserServiceException("L'utente con id " + id + " non esiste");
        }
        return user;
    }

    public void createUser(String username, String password, UserRole role, String nome, String cognome) {
        userDAO.newUser(username, password, role, nome, cognome);
    }

    public void updateUserInfo(int id, String nome, String cognome, String indirizzo, String cap, String provincia, String stato) {
        User user = userDAO.searchById(id);

        if (user.isDeleted()) {
            throw new UserServiceException("L'utente è eliminato");
        }

        userDAO.UpdateUserInfo(id, nome, cognome, indirizzo, cap, provincia, stato);
    }

    public void updateRole(int id) {
        User user = userDAO.searchById(id);

        if (user.isDeleted()) {
            throw new UserServiceException("L'utente è eliminato");
        }

        UserRole newRole = user.getRole() == UserRole.ADMIN ? UserRole.USER : UserRole.ADMIN;
        userDAO.UpdateRole(id, newRole);
    }

    public void deleteUser(int id) {

        User user = userDAO.searchById(id);
        SessionManager sessionManager = SessionManager.getInstance();

        if (user.isDeleted()) {
            throw new UserServiceException("L'utente è eliminato");
        }

        if (sessionManager.getInstance().isAdmin() && sessionManager.getCurrentUser().getId() == id) {
            throw new UserServiceException("Non puoi eliminare il tuo profilo");
        }

        // se passa tutti i controlli, procedi
        userDAO.deleteUser(id);
    }
}
