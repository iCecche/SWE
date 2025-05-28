package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import db.ProductDAOImplementation;
import model.Prodotto;

public class ProdottiPanel extends JPanel {

    private JPanel panelBottoni;
    private JTable tabella;
    private DefaultTableModel modello;
    private ProductDAOImplementation prodottoDAO;

    public ProdottiPanel(boolean isAdmin) {
        setUpPanel();
        load_data();

        if(!isAdmin)
            panelBottoni.setVisible(false);
    }

    public ProdottiPanel(int prodottoId) {
        setUpPanel();
        load_data(prodottoId);
        panelBottoni.setVisible(false);
    }

    private void setUpPanel() {
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Tabella
        modello = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrizione", "Prezzo", "Stock"}, 0);
        tabella = new JTable(modello);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        createButtons();
        add(panelBottoni, BorderLayout.SOUTH);

        prodottoDAO = new ProductDAOImplementation();
    }

    private void createButtons() {
        // Pulsanti
        panelBottoni = new JPanel();

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
    }

    private void addButtonCollback(ActionEvent e) {
        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Nome", "Nome del prodotto...");
        campi.put("Descrizione", "Descrizione del prodotto...");
        campi.put("Prezzo", "Prezzo del prodotto...");
        campi.put("Quantità", "Quantitò del prodotto...");

        // Mostra dialog dinamico
        new Dialog(null,"Modifica Prodotto", campi, valoriInseriti -> {
            // Aggiornamento del prodotto
            String nome = valoriInseriti.get("Nome");
            String descrizione = valoriInseriti.get("Descrizione");
            int prezzo = Integer.parseInt(valoriInseriti.get("Prezzo"));
            int quantity = Integer.parseInt(valoriInseriti.get("Quantità"));

            prodottoDAO.insert(nome, descrizione, prezzo, quantity);
            load_data();
        });
    }

    private void modifyButtonCollback(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Seleziona un prodotto.");
            return;
        }

        // Recupero ID prodotto selezionato
        int id = (Integer) modello.getValueAt(selected, 0);

        // Recupero prodotto dal DB
        List<Prodotto> prods = prodottoDAO.searchById(id);

        if (prods.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Prodotto non trovato.");
            return;
        }

        Prodotto prod = prods.getFirst();

        // Costruzione dinamica dei campi da mostrare
        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Nome", prod.getName());
        campi.put("Descrizione", prod.getDescription());
        campi.put("Prezzo", prod.getPrice());
        campi.put("Quantità", prod.getStock());

        // Mostra dialog dinamico
        new Dialog(null,"Modifica Prodotto", campi, valoriInseriti -> {
            // Aggiornamento del prodotto
            String nome = valoriInseriti.get("Nome");
            String descrizione = valoriInseriti.get("Descrizione");
            int prezzo = Integer.parseInt(valoriInseriti.get("Prezzo"));
            int quantity = Integer.parseInt(valoriInseriti.get("Quantità"));

            prodottoDAO.update(id, nome, descrizione, prezzo, quantity);
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
        prodottoDAO.delete(id);
        load_data();
    }

    private void load_data() {
        modello.setRowCount(0);
        List<Prodotto> lista = prodottoDAO.searchAll();
        for (Prodotto p : lista) {
            modello.addRow(new Object[]{
                    p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock()
            });
        }
    }

    private void load_data(int prodottoId) {
        modello.setRowCount(0);
        List<Prodotto> lista = prodottoDAO.searchById(prodottoId);
        for (Prodotto p : lista) {
            modello.addRow(new Object[]{
                    p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock()
            });
        }
    }
}