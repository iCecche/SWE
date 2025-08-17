package ui;

import model.User;
import ui.base.BaseDashboard;

public class UserDashboard extends BaseDashboard {

    public UserDashboard(User user) {
        super(user);
    }

    @Override
    protected void createSidebar() {
        addSidebarButton("🏠 Home", e -> showHome());
        addSidebarButton("📦 Prodotti", e -> showProducts());
        addSidebarButton("🧾 I miei Ordini", e -> showOrders());
        addSidebarButton("👤 Profilo", e -> showProfile());
        addSidebarButton("🚪 Logout", e -> logout());
    }
}
