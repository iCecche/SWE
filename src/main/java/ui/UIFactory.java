package ui;

import model.User;
import ui.base.BaseDashboard;

public class UIFactory {
    public static BaseDashboard createDashboard(User user) {
        return switch (user.getRole()) {
            case ADMIN -> new AdminDashboard(user);
            case USER -> new UserDashboard(user);
        };
    }
}

