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
    private JTable tabella;
    private DefaultTableModel modello;
    private JPanel panelBottoni;
    private UserDAOImplementation userDAO;

    public UsersPanel() {
        setUpPanel();
        load_data();
    }

    public UsersPanel(int userId ) {
        setUpPanel();
        load_data(userId);
    }

    public UsersPanel(Consumer<Integer> onUserSelected) {
        this();
        panelBottoni.setVisible(false);
        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabella.getSelectedRow();
                    if (row != -1) {
                        int userId = (int) modello.getValueAt(row, 0);
                        onUserSelected.accept(userId);
                    }
                }
            }
        });
    }

    private void setUpPanel() {
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Tabella
        modello = new DefaultTableModel(new Object[]{"ID", "Username", "Nome", "Cognome", "Indirizzo", "Cap", "Provincia", "Stato"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo la terza colonna è modificabile
            };
        };
        tabella = new JTable(modello);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        // Pulsanti
        createButtons();
        userDAO = new UserDAOImplementation();
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
        btnAggiungi.addActionListener(this::addButtonCallback);
        btnModifica.addActionListener(this::modifyButtonCallback);
        btnElimina.addActionListener(this::deleteButtonCallback);
        btnRole.addActionListener(this::roleButtonCallback);

        add(panelBottoni, BorderLayout.SOUTH);
    }

    private void addButtonCallback(ActionEvent e) {
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
            load_data();
        });
    }

    private void modifyButtonCallback(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Seleziona un prodotto.");
            return;
        }
        int id = (Integer) modello.getValueAt(selected, 0);
        User user = userDAO.searchUserInfoById(id);

        if(user == null) {
            JOptionPane.showMessageDialog(null, "Utente non trovato.");
            return;
        }

        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Username", user.getUsername());
        campi.put("Password", user.getPassword());
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
            load_data();
        });
    }

    private void deleteButtonCallback(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Seleziona un prodotto.");
            return;
        }
        int id = (Integer) modello.getValueAt(selected, 0);
        userDAO.deleteUser(id);
        load_data();
    }

    private void roleButtonCallback(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if(selected == -1) {
            JOptionPane.showMessageDialog(null, "Seleziona un utente.");
            return;
        }
        int id = Integer.parseInt(tabella.getValueAt(selected, 0).toString());
        User user = userDAO.searchUserInfoById(id);
        if(user == null) {
            JOptionPane.showMessageDialog(null, "Utente non trovato.");
            return;
        }

        UserRole role = user.getRole();
        if(role == UserRole.ADMIN) {
            role = UserRole.USER;
        }else {
            role = UserRole.ADMIN;
        }

        userDAO.UpdateRole(id, role);
    }

    private void setContent(Component comp, Container parent) {
        parent.removeAll();
        parent.add(comp, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }

    private void load_data() {
        modello.setRowCount(0);
        List<User> userList = userDAO.searchUsersInfo();
        for (User user : userList) {
            modello.addRow(new Object[]{
                    user.getId(),user.getUsername(), user.getNome(), user.getCognome(), user.getIndirizzo(), user.getCap(), user.getProvincia(), user.getStato()
            });
        }
    }

    private void load_data(int userId) {
        modello.setRowCount(0);
        User user = userDAO.searchUserInfoById(userId);
        if(user == null) {
            JOptionPane.showMessageDialog(null, "Utente non trovato.");
            return;
        }
        modello.addRow(new Object[]{
                user.getId(),user.getUsername(), user.getNome(), user.getCognome(), user.getIndirizzo(), user.getCap(), user.getProvincia(), user.getStato()
        });
    }
}
