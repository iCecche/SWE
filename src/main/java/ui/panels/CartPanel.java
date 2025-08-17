package ui.panels;

import db.OrdineDAOImplementation;
import db.ProductDAOImplementation;
import model.DettaglioOrdine;
import model.Ordine;
import model.OrdineBuilder;
import model.Prodotto;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import ui.base.BasePanel;
import ui.base.UIContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.List;

public class CartPanel extends BasePanel {
    private JTable productsTable, cartTable;
    private DefaultTableModel cartModel, productsModel;
    private OrdineDAOImplementation ordineDAO;
    private ProductDAOImplementation prodottoDAO;

    private final int targetUserId;

    public CartPanel(UIContext uiContext, int targetUserId) {
        super(uiContext);
        this.targetUserId = targetUserId;
    }

    @Override
    protected void initializeComponents() {
        prodottoDAO = new ProductDAOImplementation();
        ordineDAO = new OrdineDAOImplementation();

        productsModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Descrizione", "Prezzo", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Prezzo", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only quantity column is editable
            }
        };

        productsTable = new JTable(productsModel);
        cartTable = new JTable(cartModel);

        createButtons();
    }

    @Override
    protected void setupLayout() {
        JPanel splitPane = new JPanel(new GridLayout(1, 2, 5, 0));
        splitPane.add(new JScrollPane(productsTable));
        splitPane.add(new JScrollPane(cartTable));

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
        if (buttonPanel != null) {
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void createButtons() {
        buttonPanel = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi al Carrello");
        JButton btnElimina = new JButton("Rimuovi dal Carrello");
        JButton btnConferma = new JButton("Conferma Ordine");

        btnAggiungi.addActionListener(this::addToCart);
        btnElimina.addActionListener(this::removeFromCart);
        btnConferma.addActionListener(this::confirmOrder);

        buttonPanel.add(btnAggiungi);
        buttonPanel.add(btnElimina);
        buttonPanel.add(btnConferma);
    }

    private void addToCart(ActionEvent e) {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleziona un prodotto dalla tabella.");
            return;
        }

        // Check if product already in cart
        Object productId = productsModel.getValueAt(selectedRow, 0);
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (cartModel.getValueAt(i, 0).equals(productId)) {
                Object value = cartModel.getValueAt(i, 3);
                int quantity = Integer.parseInt(value.toString()) + 1;
                cartModel.setValueAt(quantity, i, 3);
                return;
            }
        }

        // Add new item to cart
        Object[] rowData = new Object[4];
        rowData[0] = productsModel.getValueAt(selectedRow, 0); // ID
        rowData[1] = productsModel.getValueAt(selectedRow, 1); // Nome
        rowData[2] = productsModel.getValueAt(selectedRow, 3); // Prezzo
        rowData[3] = 1; // Quantity

        cartModel.addRow(rowData);
    }

    private void removeFromCart(ActionEvent e) {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleziona un prodotto dal carrello.");
            return;
        }

        cartModel.removeRow(selectedRow);
    }

    private void confirmOrder(ActionEvent e) {
        if (cartModel.getRowCount() == 0) {
            showError("Il carrello è vuoto.");
            return;
        }

        if (!confirmAction("Confermi l'ordine?")) return;

        try {
            OrdineBuilder builder = OrdineBuilder.create();
            builder.withUserId(targetUserId);
            builder.withDate(new Timestamp(System.currentTimeMillis()));
            builder.withPaymentStatus(PaymentStatus.PENDING);
            builder.withDeliveryStatus(DeliveryStatus.PENDING);

            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int productId = (int) cartModel.getValueAt(i, 0);
                int quantity = Integer.parseInt(cartModel.getValueAt(i, 3).toString());

                DettaglioOrdine details = new DettaglioOrdine();
                details.setProduct_id(productId);
                details.setQuantity(quantity);

                builder.withDetails(details);
            }

            Ordine ordine = builder.build();
            ordineDAO.insertNewOrder(ordine);

            showInfo("Ordine creato con successo!");

            // Return to orders panel
            Container parent = getParent();
            if (parent != null) {
                setContent(new OrdiniPanel(uiContext), parent);
            }

        } catch (Exception ex) {
            showError("Errore nella creazione dell'ordine: " + ex.getMessage());
        }
    }

    @Override
    protected void loadData() {
        productsModel.setRowCount(0);
        List<Prodotto> products = prodottoDAO.searchAll();

        for (Prodotto p : products) {
            if (!p.isDeleted()) {
                productsModel.addRow(new Object[]{
                        p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock()
                });
            }
        }
    }
}