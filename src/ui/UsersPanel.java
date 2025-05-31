package ui;

import db.UserDAOImplementation;
import model.User;
import model.UserRole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UsersPanel extends JPanel {

    private Integer userId;
    private JTable tabella;
    private DefaultTableModel modello;
    private JPanel panelBottoni;
    private UserDAOImplementation userDAO;

    public UsersPanel() {
        this((Integer) null); // call UserPanel(Integer userId)
    }

    public UsersPanel(Integer userId) {
        this.userId = userId;
        this.userDAO = new UserDAOImplementation();
        setUpPanel();
    }

    public UsersPanel(Consumer<Integer> onUserSelected) {
        this((Integer) null);
        panelBottoni.setVisible(false);
        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabella.getSelectedRow();
                    if (row != -1) {
                        int userId = getSelectedUserId(row);
                        onUserSelected.accept(userId);
                    }
                }
            }
        });
    }

    private boolean isSingleView() {
        return userId != null;
    }

    private void setUpPanel() {
        configurePanelLayout();

        // Tabella
        modello = createTableModel(new String[]{"ID", "Username", "Nome", "Cognome", "Indirizzo", "Cap", "Provincia", "Stato"});
        tabella = new JTable(modello);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        // Pulsanti
        setupButtons();
        loadData();
    }

    private void configurePanelLayout() {
        setSize(800, 500);
        setLayout(new BorderLayout());
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupButtons() {
        if(!isSingleView()) {
            createButtons();
            add(panelBottoni, BorderLayout.SOUTH);
        }
    }

    private void createButtons() {
        // Pulsanti
        panelBottoni = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnModifica = new JButton("Modifica");
        JButton btnElimina = new JButton("Elimina");
        JButton btnRole = new JButton("Role");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnModifica);
        panelBottoni.add(btnElimina);
        panelBottoni.add(btnRole);

        // Eventi
        btnAggiungi.addActionListener(this::onAdd);
        btnModifica.addActionListener(this::onModify);
        btnElimina.addActionListener(this::onDelete);
        btnRole.addActionListener(this::onRoleButton);
    }

    private void onAdd(ActionEvent e) {
        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Username", "Username...");
        campi.put("Password", "Password...");
        campi.put("Nome", "Nome utente...");
        campi.put("Cognome", "Cognome utente...");
        campi.put("Role", "ADMIN or USER");

        // Mostra dialog dinamico
        new Dialog(null,"Aggiungi Utente", campi, valoriInseriti -> {

            String username = valoriInseriti.get("Username");
            String password = valoriInseriti.get("Password");
            String nome = valoriInseriti.get("Nome");
            String cognome = valoriInseriti.get("Cognome");
            UserRole role = UserRole.fromString(valoriInseriti.get("Role"));

            userDAO.insert(username, password, role, nome, cognome);
            loadData();
        });
    }

    private void onModify(ActionEvent e) {
        User user = getUserFromTable();
        if(user == null) {
            mostraErrore("Utente non trovato.");
            return;
        }

        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Username", user.getUsername());
        campi.put("Nome", user.getNome());
        campi.put("Cognome", user.getCognome());
        campi.put("Indirizzo", user.getIndirizzo());
        campi.put("Cap", user.getCap());
        campi.put("Provincia", user.getProvincia());
        campi.put("Stato",user.getStato());
        campi.put("Role", user.getRole().toString());

        // Mostra dialog dinamico
        new Dialog(null,"Modifica Utente", campi, valoriInseriti -> {

            String nome = valoriInseriti.get("Nome");
            String cognome = valoriInseriti.get("Cognome");
            String indirizzo = valoriInseriti.get("Indirizzo");
            String cap = valoriInseriti.get("Cap");
            String provincia = valoriInseriti.get("Provincia");
            String stato = valoriInseriti.get("Stato");

            userDAO.UpdateUserInfo(user.getId(), nome, cognome, indirizzo, cap, provincia, stato);
            loadData();
        });
    }

    private void onDelete(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            mostraErrore("Seleziona un utente.");
            return;
        }
        int id = getSelectedUserId(selected);
        userDAO.deleteUser(id);
        loadData();
    }

    private void onRoleButton(ActionEvent e) {
        User user = getUserFromTable();
        if(user == null) {
            mostraErrore("Utente non trovato.");
            return;
        }

        UserRole role = user.getRole();
        role = toggleRole(role);

        userDAO.UpdateRole(user.getId(), role);
    }

    private UserRole toggleRole(UserRole role) {
        if(role == UserRole.ADMIN) {
            role = UserRole.USER;
        }else {
            role = UserRole.ADMIN;
        }
        return role;
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio);
    }

    private User getUserFromTable() {
        int selected = tabella.getSelectedRow();
        if(selected == -1) {
            mostraErrore("Seleziona un utente.");
            return null;
        }

        int id = getSelectedUserId(selected);
        return userDAO.searchUserInfoById(id);
    }

    private int getSelectedUserId(int selected_row) {
        return Integer.parseInt(tabella.getValueAt(selected_row, 0).toString());
    }

    private void loadData() {
        modello.setRowCount(0);

        if(isSingleView()) {
            User user = userDAO.searchUserInfoById(userId);
            if(user == null) {
                mostraErrore("Utente non trovato.");
                return;
            }
            addUserToTable(user);
        }else {
            List<User> users = userDAO.searchUsersInfo();
            users.forEach(this::addUserToTable);
        }
    }

    private void addUserToTable(User user) {
        Object[] rowData = new Object[]{user.getId(), user.getUsername(), user.getNome(), user.getCognome(), user.getIndirizzo(), user.getCap(), user.getProvincia(), user.getStato()};
        modello.addRow(rowData);
    }
}
