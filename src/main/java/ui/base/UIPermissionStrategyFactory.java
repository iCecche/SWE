package ui.base;

import model.enums.UserRole;

public class UIPermissionStrategyFactory {
    public static UIPermissionStrategy create(UserRole role) {
        return switch (role) {
            case ADMIN -> new AdminPermissionStrategy();
            case USER -> new UserPermissionStrategy();
        };
    }
}
