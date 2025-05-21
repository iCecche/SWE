package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import db.ProductDAOImplementation;
import model.Prodotto;

public class DashboardUI extends JFrame {
    private JTable tabella;
    private DefaultTableModel modello;
    private ProductDAOImplementation prodottoDAO;

    public DashboardUI() {
        setTitle("Gestione Magazzino");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // Tabella
        modello = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrizione", "Prezzo", "Stock"}, 0);
        tabella = new JTable(modello);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        // Pulsanti
        JPanel panelBottoni = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnModifica = new JButton("Modifica");
        JButton btnElimina = new JButton("Elimina");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnModifica);
        panelBottoni.add(btnElimina);

        add(panelBottoni, BorderLayout.SOUTH);

        prodottoDAO = new ProductDAOImplementation();

        // Eventi
        btnAggiungi.addActionListener(e -> {
            ProdottoDialog dialog = new ProdottoDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isSalvato()) {
                Prodotto new_prod = dialog.getProdotto();
                prodottoDAO.insert(new_prod.getName(), new_prod.getDescription(), new_prod.getPrice(), new_prod.getStock());
                load_data();
            }
        });

        btnModifica.addActionListener(e -> {
            int selected = tabella.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un prodotto.");
                return;
            }
            int id = (Integer) modello.getValueAt(selected, 0);
            List<Prodotto> prods = prodottoDAO.searchById(id);
            ProdottoDialog dialog = new ProdottoDialog(this, prods.get(0));
            dialog.setVisible(true);
            if (dialog.isSalvato()) {
                Prodotto prod = dialog.getProdotto();
                prodottoDAO.update(prod.getId(), prod.getName(), prod.getDescription(), prod.getPrice(), prod.getStock());
                load_data();
            }
        });

        btnElimina.addActionListener(e -> {
            int selected = tabella.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un prodotto.");
                return;
            }
            int id = (Integer) modello.getValueAt(selected, 0);
            prodottoDAO.delete(id);
            load_data();
        });

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
}
