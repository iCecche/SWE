package ui.base;

import model.User;

public class UIContext {
    private final User currentUser;
    private final UIPermissionStrategy permissionStrategy;

    public UIContext(User user) {
        this.currentUser = user;
        this.permissionStrategy = UIPermissionStrategyFactory.create(user.getRole());
    }

    public User getCurrentUser() { return currentUser; }
    public UIPermissionStrategy getPermissionStrategy() { return permissionStrategy; }
    public boolean isAdmin() { return permissionStrategy.isAdmin(); }
    public int getUserId() { return currentUser.getId(); }

}
