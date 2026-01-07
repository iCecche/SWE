package services;

import model.User;
import ui.base.UIPermissionStrategy;
import ui.base.UIPermissionStrategyFactory;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private UIPermissionStrategy permissionStrategy;

    private SessionManager() {
        // Costruttore privato per Singleton
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        this.permissionStrategy = UIPermissionStrategyFactory.create(user.getRole());
    }

    public void logout() {
        this.currentUser = null;
        this.permissionStrategy = null;
    }

    public boolean isLogged() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public UIPermissionStrategy getPermissions() { return permissionStrategy; }

    // Utile per controllare i permessi velocemente
    public boolean isAdmin() {
        return isLogged() && currentUser.getRole() == model.enums.UserRole.ADMIN;
    }
}
