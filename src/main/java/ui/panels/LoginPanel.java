package ui.panels;

import model.exceptions.AuthServiceException;
import orm.UserDAOImplementation;
import model.User;
import services.AuthService;
import services.SessionManager;
import ui.UIFactory;

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

    private AuthService authService;

    public LoginPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setVisible(true);
    }

    private void initializeComponents() {
        login_page_label = new JLabel("Login Page");
        login_page_label.setBounds(215, 75, 100, 20);
        login_page_label.setFont(login_page_label.getFont().deriveFont(Font.BOLD, 14));

        username_label = new JLabel("Username:");
        username_label.setBounds(100, 150, 100, 30);

        username_field = new JTextField();
        username_field.setBounds(215, 150, 200, 30);
        username_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        username_field.setBorder(BorderFactory.createCompoundBorder(
                username_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        username_field.setEditable(true);

        password_label = new JLabel("Password:");
        password_label.setBounds(100, 200, 100, 30);

        password_field = new JPasswordField();
        password_field.setBounds(215, 200, 200, 30);
        password_field.setEditable(true);
        password_field.setEchoChar('*');
        password_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        password_field.setBorder(BorderFactory.createCompoundBorder(
                password_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        register_button = new JButton("Register");
        register_button.setBounds(125, 300, 100, 30);
        register_button.setOpaque(true);
        register_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

        login_button = new JButton("Login");
        login_button.setBounds(275, 300, 100, 30);
        login_button.setBackground(new Color(0, 149, 255));
        login_button.setOpaque(true);
        login_button.setForeground(Color.WHITE);
        login_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
    }

    private void setupLayout() {
        setTitle(" Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        add(login_page_label);
        add(username_label);
        add(username_field);
        add(password_label);
        add(password_field);
        add(register_button);
        add(login_button);
    }

    private void setupEventListeners() {
        login_button.addActionListener(e -> performLogin());
        password_field.addActionListener(e -> performLogin()); // ENTER key triggers login

        register_button.addActionListener(e -> {
            dispose();
            new RegisterPanel();
        });
    }

    private void performLogin() {
        String username = username_field.getText().trim();
        String password = new String(password_field.getPassword());

        // User authentication
        authService = new AuthService();
        try {
            User user = authService.login(username, password);
            SessionManager.getInstance().login(user);
            // If authentication succeeds, open appropriate dashboard
            JOptionPane.showMessageDialog(this, "Login effettuato!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            openDashboard(user);
        }catch (AuthServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private void openDashboard(User user) {
        dispose();
        UIFactory.createDashboard(user);
    }
}