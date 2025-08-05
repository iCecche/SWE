package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserDashboard extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private User user;

    public UserDashboard(User user) {
        setTitle("📦 Dashboard Magazzino");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridLayout(0, 1, 0, 10));
        sidebarPanel.setBackground(new Color(30, 30, 30));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Pulsanti menu
        addSidebarButton("🏠 Home", e -> showHome());
        addSidebarButton("📦 Prodotti", e -> {
            JPanel panel = new ProdottiPanel(false); // TODO: vietare modifiche al db se role == utente
            setContent(panel);
        });
        addSidebarButton("🧾 Ordini", e -> {
            JPanel panel = new OrdiniPanel(user.getId()); // TODO: vietare modifiche al db se role == utente
            setContent(panel);
        });
        addSidebarButton("🚪 Logout", e -> logout());

        // Pannello contenuti
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Aggiunta pannelli
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        this.user = user;

        showHome();
        setVisible(true);
    }

    private void addSidebarButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(45, 45, 45));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        // On hover
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

        sidebarPanel.add(button);
    }

    private void showHome() {
        setContent(new JLabel("Benvenuto nella Dashboard!", SwingConstants.CENTER));
    }

    private void setContent(Component comp) {
        contentPanel.removeAll();
        contentPanel.add(comp, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Vuoi davvero uscire?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPanel(); // oppure torna alla tua login
        }
    }
}
