package ui;

import db.OrdineDAOImplementation;
import db.StatoOrdineDAOImplementation;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OrdiniPanel extends JPanel {

    private JPanel panelBottoni;
    private JTable tabella;
    private DefaultTableModel modello;
    private OrdineDAOImplementation ordineDAO;
    private Integer userId;

    public OrdiniPanel() {
        Object[] table_model = new Object[]{"Order ID", "User ID", "Date", "Product ID", "Quantity", "Order Status", "Payment Status"};
        setUpPanel(table_model, null);
        load_data();
    }

    public OrdiniPanel(int userId) {
        this.userId = userId;
        Object[] table_model = new Object[]{"Order ID", "Date", "Product ID", "Quantity", "Order Status", "Payment Status"};
        setUpPanel(table_model, userId);
        load_data(userId);
    }

    private void setUpPanel(Object[] table_model, Integer userId) {
        setSize(800, 500);
        setLayout(new BorderLayout());

        if(isUser())
            createUserButtons();
        else {
            createAdminButtons();
        }
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
        tabella.convertColumnIndexToModel(col);
        if (tabella.getColumnName(col).equals("User ID")) {
            Object value = tabella.getValueAt(row, col);
            if (value != null) {
                int userId = Integer.parseInt(value.toString());

                // Esegui query per quel userId
                System.out.println("Hai cliccato sull'user_id: " + userId);

                // Esempio: apri un nuovo pannello o aggiorna la tabella
                mostraDettagliUtente(userId, getParent());
            }
        }
    }

    private void ProdottoIdCellCallback(int row, int col) {
        tabella.convertColumnIndexToModel(col);
        if (tabella.getColumnName(col).equals("Product ID")) {
            Object value = tabella.getValueAt(row, col);
            if (value != null) {
                int prodottoId = Integer.parseInt(value.toString());

                // Esegui query per quel userId
                System.out.println("Hai cliccato sull'prodotto: " + prodottoId);

                // Esempio: apri un nuovo pannello o aggiorna la tabella
                mostraDettaglioProdotto(prodottoId, getParent());
            }
        }
    }

    private void createAdminButtons() {
        // Pulsanti
        panelBottoni = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnElimina = new JButton("Elimina");
        JButton btnSpedisci = new JButton("Spedisci");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnElimina);
        panelBottoni.add(btnSpedisci);

        btnAggiungi.addActionListener(this::adminAddButtonCallback);
        btnElimina.addActionListener(this::deleteButtonCallback);
        btnSpedisci.addActionListener(this::shipButtonCallback);
    }

    private void createUserButtons() {
        panelBottoni = new JPanel();
        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnPaga = new JButton("Paga");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnPaga);

        btnAggiungi.addActionListener(this::userAddButtonCallback);
        btnPaga.addActionListener(this::pagaButtonCallback);
    }

    private void adminAddButtonCallback(ActionEvent e) {
        Container parent = getParent();
        mostraUserSelection(parent);
    }

    private void userAddButtonCallback(ActionEvent e) {
        Container parent = getParent();
        mostraCarrello(userId, parent);
    }

    private void deleteButtonCallback(ActionEvent e) {
        int selectedRow = tabella.getSelectedRow();
        if(selectedRow == -1 ) {
            JOptionPane.showMessageDialog(null, "Seleziona un ordine.");
            return;
        }

        int id = (int) modello.getValueAt(selectedRow, 0);
        ordineDAO.deleteOrder(id);
        load_data();
    }

    private void pagaButtonCallback(ActionEvent e) {
        int selectedRow = tabella.getSelectedRow();
        if(selectedRow == -1 ) {
            JOptionPane.showMessageDialog(null, "Seleziona un ordine.");
            return;
        }

        String paymentStatus = (String) tabella.getValueAt(selectedRow, 5);
        PaymentStatus status = PaymentStatus.fromString(paymentStatus);

        if(status == PaymentStatus.PAID) {
            JOptionPane.showMessageDialog(null, "L'ordine è già stato pagato.");
            return;
        }

        int order_id = (int) modello.getValueAt(selectedRow, 0);
        StatoOrdine new_order_status = new StatoOrdine();
        new_order_status.setOrder_id(order_id);
        new_order_status.setPayment_status(PaymentStatus.PAID);

        StatoOrdineDAOImplementation statoOrdineDAO = new StatoOrdineDAOImplementation();
        statoOrdineDAO.updatePaymentStatus(new_order_status);

        System.out.println("L'ordine pagato con successo.");
        load_data(userId);
    }

    private void shipButtonCallback(ActionEvent e) {
        int selectedRow = tabella.getSelectedRow();
        if(selectedRow == -1 ) {
            JOptionPane.showMessageDialog(null, "Seleziona un ordine.");
            return;
        }

        String order_status = (String) tabella.getValueAt(selectedRow, 5);
        OrderStatus status = OrderStatus.fromString(order_status);

        if(status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            JOptionPane.showMessageDialog(null, "L'ordine è già stato gestito.");
            return;
        }

        int order_id = (int) modello.getValueAt(selectedRow, 0);
        StatoOrdine new_order_status = new StatoOrdine();
        new_order_status.setOrder_id(order_id);
        new_order_status.setOrder_status(OrderStatus.SHIPPED);

        StatoOrdineDAOImplementation statoOrdineDAO = new StatoOrdineDAOImplementation();
        statoOrdineDAO.updateOrderStatus(new_order_status);

        System.out.println("L'ordine è stato gestito con successo.");
        load_data();
    }

    private void load_data() {
        modello.setRowCount(0);
        List<Ordine> lista = ordineDAO.searchAll();
        for (Ordine ordine : lista) {
            modello.addRow(new Object[]{
                    ordine.getOrder_id(), ordine.getUser_id(), ordine.getDate(), ordine.getProduct_id(), ordine.getQuantity(), ordine.getStato_consegna(), ordine.getStato_pagamento()
            });
        }
    }

    private void load_data(int userId) {
        modello.setRowCount(0);
        List<Ordine> lista = ordineDAO.searchByUserID(userId);
        for (Ordine ordine : lista) {
            modello.addRow(new Object[]{
                    ordine.getOrder_id(), ordine.getDate(), ordine.getProduct_id(), ordine.getQuantity(), ordine.getStato_consegna(), ordine.getStato_pagamento()
            });
        }
    }

    public void mostraDettagliUtente(int userId, Container parent) {
        setContent(new UsersPanel(userId), parent);
    }

    public void mostraDettaglioProdotto(int prodottoId, Container parent) {
        setContent(new ProdottiPanel(prodottoId), parent);
    }

    public void mostraUserSelection(Container parent) {
        setContent(new UsersPanel(userId -> mostraCarrello(userId, parent)), parent);
    }

    public void mostraCarrello(int userId, Container parent) {
        setContent(new CartPanel(userId), parent);
    }

    private void setContent(Component comp, Container parent) {
        parent.removeAll();
        parent.add(comp, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }

    private boolean isUser() {
        return userId != null;
    }
}
