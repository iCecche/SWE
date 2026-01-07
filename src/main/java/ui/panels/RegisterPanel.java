package ui.panels;

import model.exceptions.AuthServiceException;
import orm.UserDAOImplementation;
import model.User;
import model.enums.UserRole;
import services.AuthService;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JFrame {
    private JLabel register_page_label;
    private JLabel username_label;
    private JLabel password_label;
    private JLabel confirm_password_label;
    private JLabel nome_label;
    private JLabel cognome_label;
    private JTextField username_field;
    private JPasswordField password_field;
    private JPasswordField confirm_password_field;
    private JTextField nome_field;
    private JTextField cognome_field;
    private JButton register_button;
    private JButton login_button;

    private AuthService authService;

    public RegisterPanel() {
        setTitle("📝 Register Page");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null); // null layout

        // --- Title ---
        register_page_label = new JLabel("Register Page");
        register_page_label.setBounds(205, 75, 120, 20);
        register_page_label.setFont(register_page_label.getFont().deriveFont(Font.BOLD, 14));
        add(register_page_label);

        // --- Username ---
        username_label = new JLabel("Username:");
        username_label.setBounds(100, 150, 100, 30);
        add(username_label);

        username_field = new JTextField();
        username_field.setBounds(215, 150, 200, 30);
        username_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        username_field.setBorder(BorderFactory.createCompoundBorder(username_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        username_field.setEditable(true);
        add(username_field);

        // --- Password ---
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

        // --- Confirm Password ---
        confirm_password_label = new JLabel("Conferma:");
        confirm_password_label.setBounds(100, 250, 100, 30);
        add(confirm_password_label);

        confirm_password_field = new JPasswordField();
        confirm_password_field.setBounds(215, 250, 200, 30);
        confirm_password_field.setEditable(true);
        confirm_password_field.setEchoChar('*');
        confirm_password_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        confirm_password_field.setBorder(BorderFactory.createCompoundBorder(confirm_password_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        add(confirm_password_field);

        // --- Nome e Cognome ---
        nome_label = new JLabel("Username:");
        nome_label.setBounds(100, 300, 100, 30);
        add(nome_label);

        nome_field = new JTextField();
        nome_field.setBounds(215, 300, 200, 30);
        nome_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        nome_field.setBorder(BorderFactory.createCompoundBorder(nome_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        nome_field.setEditable(true);
        add(nome_field);

        cognome_label = new JLabel("Cognome:");
        cognome_label.setBounds(100, 350, 100, 30);
        add(cognome_label);

        cognome_field = new JTextField();
        cognome_field.setBounds(215, 350, 200, 30);
        cognome_field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        cognome_field.setBorder(BorderFactory.createCompoundBorder(cognome_field.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        cognome_field.setEditable(true);
        add(cognome_field);

        // --- Buttons ---
        register_button = new JButton("Register");
        register_button.setBounds(275, 420, 100, 30);
        register_button.setOpaque(true);
        register_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        register_button.setBackground(new Color(0, 149, 255));
        register_button.setForeground(Color.WHITE);
        add(register_button);

        login_button = new JButton("Login");
        login_button.setBounds(125, 420, 100, 30);
        login_button.setOpaque(true);
        login_button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        add(login_button);

        // --- Action Listeners ---
        register_button.addActionListener(e ->  {
            handleRegister();
        });

        login_button.addActionListener(e -> {
            dispose(); // chiude questa finestra
            new LoginPanel().setVisible(true); // mostra login
        });

        setVisible(true);
    }

    private void handleRegister() {
        String username = username_field.getText().trim();
        String password = new String(password_field.getPassword());
        String confirmPassword = new String(confirm_password_field.getPassword());
        String nome = nome_field.getText().trim();
        String cognome = cognome_field.getText().trim();

        authService = new AuthService();

        try {
            authService.register(username, password, confirmPassword, UserRole.USER, nome, cognome);
        }catch (AuthServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(this, "Registrazione completata!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginPanel().setVisible(true);
    }
}
