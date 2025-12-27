package services;

import model.User;
import model.enums.UserRole;
import model.exceptions.UserAlreadyExists;
import model.exceptions.UserNotFound;
import orm.UserDAOImplementation;

import javax.swing.*;

public class AuthService {
    private final UserDAOImplementation userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImplementation();
    }

    public AuthService(UserDAOImplementation userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) {
        User user = userDAO.searchByUsername(username);
        // User authentication
        if (user == null || user.isDeleted()) {
            throw new UserNotFound("Utente non registrato.");
        }

        if (!user.getPassword().equals(password)) {
            throw new UserNotFound("Password errata.");
        }

        return user;
    };

    public void register(String username, String password, UserRole role, String nome, String cognome) {
        User user = userDAO.searchByUsername(username);

        if (user != null) {
            throw new UserAlreadyExists("Utente già registrato");
        }

        Long newUser_Id = userDAO.newUser(username, password, role, nome, cognome);

        if(newUser_Id == null) {
            throw new RuntimeException("Errore durante la registrazione");
        }
        return;
    };
}

