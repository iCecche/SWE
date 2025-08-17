package ui;


import model.User;
import ui.base.BaseDashboard;

public class AdminDashboard extends BaseDashboard {

    public AdminDashboard(User user) {
        super(user);
    }

    @Override
    protected void createSidebar() {
        addSidebarButton("🏠 Home", e -> showHome());
        addSidebarButton("📦 Prodotti", e -> showProducts());
        addSidebarButton("🧾 Ordini", e -> showOrders());
        addSidebarButton("👥 Utenti", e -> showUsers());
        addSidebarButton("🚪 Logout", e -> logout());
    }
}

