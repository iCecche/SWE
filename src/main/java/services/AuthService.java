package services;

import model.User;
import model.enums.UserRole;
import model.exceptions.AuthServiceException;
import orm.UserDAOImplementation;

public class AuthService {
    private final UserDAOImplementation userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImplementation();
    }

    public AuthService(UserDAOImplementation userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) {

        if (username.isEmpty() || password.isEmpty()) {
            throw new AuthServiceException("Compila tutti i campi");
        }

        User user = userDAO.searchByUsername(username);
        // User authentication
        if (user == null || user.isDeleted()) {
            throw new AuthServiceException("Utente non registrato.");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthServiceException("Password errata.");
        }

        return user;
    };

    public void register(String username, String password, String confirmPassword, UserRole role, String nome, String cognome) {

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new AuthServiceException("Compila tutti i campi");
        }

        if (!password.equals(confirmPassword)) {
            throw new AuthServiceException("Le password non coincidono");
        }

        User user = userDAO.searchByUsername(username);

        if (user != null) {
            throw new AuthServiceException("Utente già registrato");
        }

        Long newUser_Id = userDAO.newUser(username, password, role, nome, cognome);

        if(newUser_Id == null) {
            throw new AuthServiceException("Errore durante la registrazione");
        }
        return;
    };
}

