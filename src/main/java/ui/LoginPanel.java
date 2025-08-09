package ui;

import db.UserDAOImplementation;
import model.User;
import model.enums.UserRole;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JFrame {

    private JLabel login_page_label;
    private JLabel username_label;
    private JLabel password_label;
    private JTextField username_field;
    private JPasswordField password_field;
    private JButton register_button;
    private JButton login_button;

    private UserDAOImplementation userDAO;

    public LoginPanel() {
        // TODO: place custom component creation code here

        login_page_label = new JLabel("Login Page");
        login_page_label.setBounds(215, 75, 100, 20);
        login_page_label.setFont(login_page_label.getFont().deriveFont(Font.BOLD, 14));
        add(login_page_label);

        username_label = new JLabel("Username:");
        username_label.setBounds(100, 150, 100, 30);
        add(username_label);

        username_field = new JTextField();
        username_field.setBounds(215, 150, 200, 30);
        username_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        username_field.setBorder(BorderFactory.createCompoundBorder(username_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        username_field.setEditable(true);
        add(username_field);

        password_label = new JLabel("Password:");
        password_label.setBounds(100, 200, 100, 30);
        add(password_label);

        password_field = new JPasswordField();
        password_field.setBounds(215, 200, 200, 30);
        password_field.setEditable(true);
        password_field.setEchoChar('*');
        password_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        password_field.setBorder(BorderFactory.createCompoundBorder(password_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        add(password_field);

        register_button = new JButton("Register");
        register_button.setBounds(125, 300, 100, 30);
        register_button.setOpaque(true);
        register_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        add(register_button);

        login_button = new JButton("Login");
        login_button.setBounds(275, 300, 100, 30);
        login_button.setBackground(new Color(0, 149, 255));
        login_button.setOpaque(true);
        login_button.setForeground(Color.WHITE);
        login_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        add(login_button);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        pack();
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        // --- Login Action ---
        login_button.addActionListener(e -> performLogin());
        // ENTER key triggers login
        password_field.addActionListener(e -> performLogin());

        // --- Register Action ---
        register_button.addActionListener(e -> {
            dispose(); // chiude la finestra login
            new RegisterPanel().setVisible(true); // mostra register
        });
    }

    private void performLogin() {
        userDAO = new UserDAOImplementation();
        String username = username_field.getText().trim();
        String pass = new String(password_field.getPassword());

        if (username.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci username e password.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // USER AUTHENTICATION
        User user = userDAO.searchByUsername(username);
        if (user == null || user.isDeleted()) {
            JOptionPane.showMessageDialog(this, "Utente non registrato.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!user.getPassword().equals(pass)) {
            JOptionPane.showMessageDialog(this, "Password errata.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // IF AUTHENTICATION SUCCEEDS, OPEN DASHBOARD
        JOptionPane.showMessageDialog(this, "Login effettuato!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        openDashboard(user);
    }

    private void openDashboard(User user) {
        dispose(); // chiude la finestra login
        if (user.getRole().equals(UserRole.ADMIN)) {
            new AdminDashboard().setVisible(true);
        }else {
            new UserDashboard(user).setVisible(true);
        }
    }
}
