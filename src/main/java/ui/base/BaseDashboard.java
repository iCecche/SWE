package ui.base;

import model.User;
import model.enums.UserRole;
import ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class BaseDashboard extends JFrame {
    protected JPanel sidebarPanel;
    protected JPanel contentPanel;
    protected UIContext uiContext;

    public BaseDashboard(User user) {
        this.uiContext = new UIContext(user);
        initializeFrame();
        setupLayout();
        createSidebar();
        showHome();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("📦 Dashboard Magazzino - " + getRoleTitle());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
    }

    private String getRoleTitle() {
        if (uiContext.getCurrentUser().getRole() == UserRole.ADMIN) {
            return "Admin";
        }
        return uiContext.getCurrentUser().getUsername();
    }

    private void setupLayout() {
        createSidebarPanel();
        createContentPanel();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridLayout(0, 1, 0, 10));
        sidebarPanel.setBackground(new Color(30, 30, 30));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
    }

    private void createContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
    }

    protected abstract void createSidebar();

    protected void addSidebarButton(String text, ActionListener action) {
        JButton button = createStyledButton(text);
        button.addActionListener(action);
        sidebarPanel.add(button);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(45, 45, 45));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(45, 45, 45));
            }
        });

        return button;
    }

    protected void showHome() {
        setContent(new JLabel("Benvenuto " + uiContext.getCurrentUser().getUsername() + "!", SwingConstants.CENTER));
    }

    protected void showProducts() {
        setContent(new ProdottiPanel(uiContext));
    }

    protected void showOrders() {
        setContent(new OrdiniPanel(uiContext));
    }

    protected void showUsers() {
        setContent(UsersPanel.createAdminUsersView(uiContext));
    }

    protected void showProfile() {
        setContent(UsersPanel.createUserProfileView(uiContext));
    }

    protected void setContent(Component comp) {
        contentPanel.removeAll();
        contentPanel.add(comp, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    protected void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Vuoi davvero uscire?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPanel();
        }
    }
}