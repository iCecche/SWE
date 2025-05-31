package ui;

import db.OrdineDAOImplementation;
import db.ProductDAOImplementation;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.List;

public class CartPanel extends JPanel {

    private boolean isAdmin = false;
    private int userId;
    private JPanel panelBottoni;
    private JTable producTable, cartTable;
    private DefaultTableModel modelloCart, modelloProdotti;
    private OrdineDAOImplementation ordineDAO;
    private ProductDAOImplementation prodottoDAO;

    public CartPanel(int userId, boolean isAdmin) {

        setSize(800, 500);
        setLayout(new BorderLayout());

        modelloProdotti = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrizione", "Prezzo", "Stock"}, 0 ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo la terza colonna è modificabile
            };
        };
        modelloCart = new DefaultTableModel(new Object[]{"ID", "Nome", "Prezzo", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // solo la terza colonna è modificabile
            };
        };

        producTable = new JTable(modelloProdotti);
        cartTable = new JTable(modelloCart);

        JScrollPane scrollPane1 = new JScrollPane(producTable);
        JScrollPane scrollPane2 = new JScrollPane(cartTable);

        JPanel splitPane = new JPanel(new GridLayout(1, 2, 0, 0)); // 1 row, 2 columns, 10px horizontal gap
        splitPane.add(scrollPane1); // Equal space initially
        splitPane.add(scrollPane2);// Set initial divider position as ratio or pixel

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);

        createButtons();

        container.setVisible(true);
        add(container, BorderLayout.CENTER);

        prodottoDAO = new ProductDAOImplementation();
        ordineDAO = new OrdineDAOImplementation();

        load_data();
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    private void createButtons() {
        // Pulsanti
        panelBottoni = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnElimina = new JButton("Elimina");
        JButton btnConferma = new JButton("Conferma");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnElimina);
        panelBottoni.add(btnConferma);

        // Eventi
        btnAggiungi.addActionListener(this::addButtonCallback);
        btnElimina.addActionListener(this::deleteButtonCallback);
        btnConferma.addActionListener(this::saveButtonCallback);

        add(panelBottoni, BorderLayout.SOUTH);
    }

    private void addButtonCallback(ActionEvent e) {
        int selectedRow = producTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select a row to copy.");
            return;
        }

        if(modelloCart.getRowCount() > 0) {
            for(int i = 0; i < modelloCart.getRowCount(); i++) {
                if(modelloCart.getValueAt(i, 0) == modelloProdotti.getValueAt(selectedRow, 0)) {
                   Object value = modelloCart.getValueAt(i, 3);
                   int quantity = Integer.parseInt(value.toString());
                   quantity++;
                   modelloCart.setValueAt(quantity, i, 3);
                   return;
                }
            }
        }

        // Convert view index to model index if sorting/filtering is used
        int modelRow = producTable.convertRowIndexToModel(selectedRow);

        // Extract values from the row
        int columnCount = modelloProdotti.getColumnCount();
        Object[] rowData = new Object[columnCount];
        int colNumber = 0;
        for (int i = 0; i < columnCount; i++) {
            if(i == 2 || i == 4) { // skip description and stock columns
                continue;
            }
            rowData[colNumber] = modelloProdotti.getValueAt(modelRow, i);
            colNumber++;
        }
        rowData[colNumber] = 1;
        // Add to second table
        modelloCart.addRow(rowData);
    }

    private void deleteButtonCallback(ActionEvent e) {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select a row to copy.");
            return;
        }

        modelloCart.removeRow(selectedRow);
    }

    private void saveButtonCallback(ActionEvent e) {
        if(modelloCart.getRowCount() > 0) {
            OrdineBuilder builder = OrdineBuilder.create();
            builder.withUserId(userId);

            Timestamp date = new Timestamp(System.currentTimeMillis());
            builder.withDate(date);

            builder.withPaymentStatus(PaymentStatus.PENDING);
            builder.withDeliveryStatus(DeliveryStatus.PENDING);

            for(int i = 0; i < modelloCart.getRowCount(); i++) {
                int product_id = (int) modelloCart.getValueAt(i, 0);
                int quantity = (int) modelloCart.getValueAt(i, 3);

                DettaglioOrdine details = new DettaglioOrdine();
                details.setProduct_id(product_id);
                details.setQuantity(quantity);

                builder.withDetails(details);
            }

            Ordine ordine = builder.build();
            ordineDAO.insertNewOrder(ordine);

            if (isAdmin)
                setContent(new OrdiniPanel(), getParent());
            else
                setContent(new OrdiniPanel(userId), getParent());
        }
    }

    private void load_data() {
        modelloProdotti.setRowCount(0);
        List<Prodotto> lista = prodottoDAO.searchAll();
        for (Prodotto p : lista) {
            modelloProdotti.addRow(new Object[]{
                    p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock()
            });
        }
    }

    private void setContent(Component comp, Container parent) {
        parent.removeAll();
        parent.add(comp, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }
}
