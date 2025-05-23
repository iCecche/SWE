package ui;

import db.UserDAOImplementation;
import model.Prodotto;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsersPanel extends JPanel {
    private JTable tabella;
    private DefaultTableModel modello;
    private UserDAOImplementation userDAO;

    public UsersPanel() {
        setUpPanel();
        load_data();
    }

    public UsersPanel(int userId ) {
        setUpPanel();
        load_data(userId);
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
        JPanel panelBottoni = createButtons();
        add(panelBottoni, BorderLayout.SOUTH);

        userDAO = new UserDAOImplementation();
    }

    private JPanel createButtons() {
        // Pulsanti
        JPanel panelBottoni = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnModifica = new JButton("Modifica");
        JButton btnElimina = new JButton("Elimina");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnModifica);
        panelBottoni.add(btnElimina);

        // Eventi
        btnAggiungi.addActionListener(this::addButtonCollback);
        btnModifica.addActionListener(this::modifyButtonCollback);
        btnElimina.addActionListener(this::deleteButtonCollback);

        return panelBottoni;
    }

    private void addButtonCollback(ActionEvent e) {
        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Username", "Nome del prodotto...");
        campi.put("Password", "Descrizione del prodotto...");

        // Mostra dialog dinamico
        new Dialog(null,"Aggiungi Utente", campi, valoriInseriti -> {

            String username = valoriInseriti.get("Username");
            String password = valoriInseriti.get("Password");

            userDAO.insert(username, password);
            load_data();
        });
    }

    private void modifyButtonCollback(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Seleziona un prodotto.");
            return;
        }
        int id = (Integer) modello.getValueAt(selected, 0);
        List<User> users = userDAO.searchUserInfoById(id);
        User user = users.getFirst();

        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Username", user.getUsername());
        campi.put("Password", user.getPassword());
        campi.put("Nome", user.getNome());
        campi.put("Cognome", user.getCognome());
        campi.put("Indirizzo", user.getIndirizzo());
        campi.put("Cap", user.getCap());
        campi.put("Provincia", user.getProvincia());
        campi.put("Stato",user.getStato());

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

    private void deleteButtonCollback(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Seleziona un prodotto.");
            return;
        }
        int id = (Integer) modello.getValueAt(selected, 0);
        userDAO.deleteUser(id);
        load_data();
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
        List<User> userList = userDAO.searchUserInfoById(userId);
        for (User user : userList) {
            modello.addRow(new Object[]{
                    user.getId(),user.getUsername(), user.getNome(), user.getCognome(), user.getIndirizzo(), user.getCap(), user.getProvincia(), user.getStato()
            });
        }
    }
}
