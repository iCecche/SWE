package ui;

import db.OrdineDAOImplementation;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OrdiniPanel extends JPanel {

    private JTable tabella;
    private DefaultTableModel modello;
    private OrdineDAOImplementation ordineDAO;

    public OrdiniPanel() {
        Object[] table_model = new Object[]{"Order ID", "User ID", "Date", "Product ID", "Quantity", "Order Status", "Payment Status"};
        setUpPanel(table_model);
        load_data();
    }

    public OrdiniPanel(int userId) {
        Object[] table_model = new Object[]{"Order ID", "Date", "Product ID", "Quantity", "Order Status", "Payment Status"};
        setUpPanel(table_model);
        load_data(userId);
    }

    private void setUpPanel(Object[] table_model) {
        setSize(800, 500);
        setLayout(new BorderLayout());

        JPanel panelBottoni = createButtons();
        add(panelBottoni, BorderLayout.SOUTH);

        tabella = createTable(table_model);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        ordineDAO = new OrdineDAOImplementation();

    }

    private DefaultTableModel createTableModel(Object[] table_model) {
        return new DefaultTableModel(table_model, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo la terza colonna è modificabile
            };
        };

    }

    private JTable createTable(Object[] table_model) {
        modello = createTableModel(table_model);
        tabella = new JTable(modello);

        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = tabella.rowAtPoint(e.getPoint());
                    int col = tabella.columnAtPoint(e.getPoint());

                    UserIdCellCallback(row, col);
                    ProdottoIdCellCallback(row, col);
                }
            }
        });
        return tabella;
    }

    private void UserIdCellCallback(int row, int col) {
        if (col == 1) {
            Object value = tabella.getValueAt(row, col);
            if (value != null) {
                int userId = Integer.parseInt(value.toString());

                // Esegui query per quel userId
                System.out.println("Hai cliccato sull'user_id: " + userId);

                // Esempio: apri un nuovo pannello o aggiorna la tabella
                mostraDettagliUtente(userId);
            }
        }
    }

    private void ProdottoIdCellCallback(int row, int col) {
        if (col == 3) {
            Object value = tabella.getValueAt(row, col);
            if (value != null) {
                int prodottoId = Integer.parseInt(value.toString());

                // Esegui query per quel userId
                System.out.println("Hai cliccato sull'prodotto: " + prodottoId);

                // Esempio: apri un nuovo pannello o aggiorna la tabella
                mostraDettaglioProdotto(prodottoId);
            }
        }
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

        return panelBottoni;
    }

    private void load_data() {
        modello.setRowCount(0);
        List<Ordine> lista = ordineDAO.searchAll();
        for (Ordine ordine : lista) {
            DettaglioOrdine dettaglio = ordine.getDettaglio();
            Prodotto prod = dettaglio.getProduct();
            StatoOrdine status = ordine.getStato();
            modello.addRow(new Object[]{
                    ordine.getId(), ordine.getUser_id(), ordine.getDate(), prod.getId(), dettaglio.getQuantity(), status.getOrder_status(), status.getPayment_status()
            });
        }
    }

    private void load_data(int userId) {
        modello.setRowCount(0);
        List<Ordine> lista = ordineDAO.searchByUserID(userId);
        for (Ordine ordine : lista) {
            DettaglioOrdine dettaglio = ordine.getDettaglio();
            Prodotto prod = dettaglio.getProduct();
            StatoOrdine status = ordine.getStato();
            modello.addRow(new Object[]{
                    ordine.getId(), ordine.getDate(), prod.getId(), dettaglio.getQuantity(), status.getOrder_status(), status.getPayment_status()
            });
        }
    }

    public void mostraDettagliUtente(int userId) {
        Container parent = getParent();
        setContent(new UsersPanel(userId), parent);
    }

    public void mostraDettaglioProdotto(int prodottoId) {
        Container parent = getParent();
        setContent(new ProdottiPanel(prodottoId), parent);
    }

    private void setContent(Component comp, Container parent) {
        parent.removeAll();
        parent.add(comp, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }
}
