package ui;

import db.OrdineDAOImplementation;
import db.ProductDAOImplementation;
import db.StatoOrdineDAOImplementation;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrdiniPanel extends JPanel {

    private List<Ordine> listaOrdini;
    private JPanel panelBottoni;
    private JTable dettaglioTable, orderTable;
    private DefaultTableModel modelloDettaglio, modelloOrdine;
    private OrdineDAOImplementation ordineDAO;
    private ProductDAOImplementation prodottoDAO;
    private Integer userId;

    public OrdiniPanel() {
        setSize(800, 500);
        setLayout(new BorderLayout());

        ProductDAOImplementation prodottoDAO = new ProductDAOImplementation();

        modelloOrdine = new DefaultTableModel(new Object[]{"Order ID", "User ID", "Date", "Delivery Status", "Payment Status"}, 0 ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        modelloDettaglio = new DefaultTableModel(new Object[]{"Product ID", "Nome", "Descrizione", "Prezzo", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        orderTable = new JTable(modelloOrdine);
        dettaglioTable = new JTable(modelloDettaglio);

        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = orderTable.getSelectedRow();
            if (row == -1) {
                return;
            }

            int orderId = Integer.parseInt(orderTable.getValueAt(row, 0).toString()); // TODO: use this style everywhere

            modelloDettaglio.setRowCount(0);
            for (Ordine ordine : listaOrdini) {
                if(ordine.getOrder_id() == orderId) {
                    List<Prodotto> prod = prodottoDAO.searchById(ordine.getProduct_id());
                    for (Prodotto prodotto : prod) {
                        modelloDettaglio.addRow(new Object[]{
                                prodotto.getId(), prodotto.getName(), prodotto.getDescription(),  prodotto.getPrice(), ordine.getQuantity()
                        });
                    }
                }
            }
        });

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = orderTable.rowAtPoint(e.getPoint());
                    int col = orderTable.columnAtPoint(e.getPoint());

                    UserIdCellCallback(row, col);
                    //ProdottoIdCellCallback(row, col);
                }
            }
        });

        JScrollPane scrollPane1 = new JScrollPane(orderTable);
        JScrollPane scrollPane2 = new JScrollPane(dettaglioTable);

        JPanel splitPane = new JPanel(new GridLayout(1, 2, 0, 0)); // 1 row, 2 columns, 10px horizontal gap
        splitPane.add(scrollPane1);
        splitPane.add(scrollPane2);

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);

        createAdminButtons();

        container.setVisible(true);
        add(container, BorderLayout.CENTER);

        ordineDAO = new OrdineDAOImplementation();
        load_data();
    }

    /*
    public OrdiniPanel() {
        Object[] table_model = new Object[]{"Order ID", "User ID", "Date", "Product ID", "Quantity", "Order Status", "Payment Status"};
        setUpPanel(table_model);
        load_data();
    }

     */

    public OrdiniPanel(int userId) {
        this.userId = userId;
        setSize(800, 500);
        setLayout(new BorderLayout());

        ProductDAOImplementation prodottoDAO = new ProductDAOImplementation();

        modelloOrdine = new DefaultTableModel(new Object[]{"Order ID", "Date", "Delivery Status", "Payment Status"}, 0 ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        modelloDettaglio = new DefaultTableModel(new Object[]{"Product ID", "Nome", "Descrizione", "Prezzo", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        orderTable = new JTable(modelloOrdine);
        dettaglioTable = new JTable(modelloDettaglio);

        // Configura la selezione della tabella ordini
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = orderTable.getSelectedRow();
            if (row == -1) {
                return;
            }
            int orderId = Integer.parseInt(orderTable.getValueAt(row, 0).toString()); // TODO: use this style everywhere

            modelloDettaglio.setRowCount(0);
            for (Ordine ordine : listaOrdini) {
                if(ordine.getOrder_id() == orderId) {
                    List<Prodotto> prod = prodottoDAO.searchById(ordine.getProduct_id());
                    for (Prodotto prodotto : prod) {
                        modelloDettaglio.addRow(new Object[]{
                                prodotto.getId(), prodotto.getName(), prodotto.getDescription(),  prodotto.getPrice(), ordine.getQuantity()
                        });
                    }
                }
            }
        });

        JScrollPane scrollPane1 = new JScrollPane(orderTable);
        JScrollPane scrollPane2 = new JScrollPane(dettaglioTable);

        JPanel splitPane = new JPanel(new GridLayout(1, 2, 0, 0)); // 1 row, 2 columns, 10px horizontal gap
        splitPane.add(scrollPane1);
        splitPane.add(scrollPane2);

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);

        createUserButtons();

        container.setVisible(true);
        add(container, BorderLayout.CENTER);

        ordineDAO = new OrdineDAOImplementation();
        load_data(userId);
    }

    private void populateDettaglioTable() {

    }


/*
    public OrdiniPanel(int userId) {
        this.userId = userId;
        Object[] table_model = new Object[]{"Order ID", "Date", "Product ID", "Quantity", "Order Status", "Payment Status"};
        setUpPanel(table_model);
        load_data(userId);
    }


    private void setUpPanel(Object[] table_model) {
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
*/
    private void UserIdCellCallback(int row, int col) {
        orderTable.convertColumnIndexToModel(col);
        if (orderTable.getColumnName(col).equals("User ID")) {
            Object value = orderTable.getValueAt(row, col);
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
        orderTable.convertColumnIndexToModel(col);
        if (orderTable.getColumnName(col).equals("Product ID")) {
            Object value = orderTable.getValueAt(row, col);
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

        add(panelBottoni, BorderLayout.SOUTH);
    }

    private void createUserButtons() {
        panelBottoni = new JPanel();
        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnPaga = new JButton("Paga");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnPaga);

        btnAggiungi.addActionListener(this::userAddButtonCallback);
        btnPaga.addActionListener(this::pagaButtonCallback);

        add(panelBottoni, BorderLayout.SOUTH);
    }

    private void adminAddButtonCallback(ActionEvent e) {
        Container parent = getParent();
        mostraUserSelection(parent);
    }

    private void userAddButtonCallback(ActionEvent e) {
        Container parent = getParent();
        mostraCarrello(userId, parent, false);
    }

    private void deleteButtonCallback(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if(selectedRow == -1 ) {
            JOptionPane.showMessageDialog(null, "Seleziona un ordine.");
            return;
        }

        int id = (int) orderTable.getValueAt(selectedRow, 0);
        ordineDAO.deleteOrder(id);
        load_data();
    }

    private void pagaButtonCallback(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if(selectedRow == -1 ) {
            JOptionPane.showMessageDialog(null, "Seleziona un ordine.");
            return;
        }

        int payment_status_col = orderTable.getColumn("Payment Status").getModelIndex();

        String paymentStatus = (String) orderTable.getValueAt(selectedRow, payment_status_col);
        PaymentStatus status = PaymentStatus.fromString(paymentStatus);

        if(status == PaymentStatus.PAID) {
            JOptionPane.showMessageDialog(null, "L'ordine è già stato pagato.");
            return;
        }

        int order_id = (int) orderTable.getValueAt(selectedRow, 0); //FIXME: style
        StatoOrdine new_order_status = new StatoOrdine();
        new_order_status.setOrder_id(order_id);
        new_order_status.setPayment_status(PaymentStatus.PAID);

        StatoOrdineDAOImplementation statoOrdineDAO = new StatoOrdineDAOImplementation();
        statoOrdineDAO.updatePaymentStatus(new_order_status);

        System.out.println("L'ordine pagato con successo.");
        load_data(userId);
    }

    private void shipButtonCallback(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if(selectedRow == -1 ) {
            JOptionPane.showMessageDialog(null, "Seleziona un ordine.");
            return;
        }

        int order_status_col = orderTable.getColumn("Delivery Status").getModelIndex();

        String order_status = (String) modelloOrdine.getValueAt(selectedRow, order_status_col); //fixme: style
        DeliveryStatus status = DeliveryStatus.fromString(order_status);

        if(status == DeliveryStatus.SHIPPED || status == DeliveryStatus.DELIVERED) {
            JOptionPane.showMessageDialog(null, "L'ordine è già stato gestito.");
            return;
        }

        int order_id = (int) modelloOrdine.getValueAt(selectedRow, 0);
        StatoOrdine new_order_status = new StatoOrdine();
        new_order_status.setOrder_id(order_id);
        new_order_status.setDelivery_status(DeliveryStatus.SHIPPED);

        StatoOrdineDAOImplementation statoOrdineDAO = new StatoOrdineDAOImplementation();
        statoOrdineDAO.updateDeliveryStatus(new_order_status);

        System.out.println("L'ordine è stato gestito con successo.");
        load_data();
    }

    private void load_data() {
        modelloOrdine.setRowCount(0);
        listaOrdini = ordineDAO.searchAll();
        Set<Integer> seenIds = new HashSet<>();
        for (Ordine ordine : listaOrdini) {
            if(!seenIds.contains(ordine.getOrder_id())) {
                modelloOrdine.addRow(new Object[]{
                        ordine.getOrder_id(), ordine.getUser_id(), ordine.getDate(), ordine.getDelivery_status(), ordine.getPayment_status()
                });
                seenIds.add(ordine.getOrder_id());
            }
        }
    }

    private void load_data(int userId) {
        modelloOrdine.setRowCount(0);
        listaOrdini = ordineDAO.searchByUserID(userId);
        Set<Integer> seenIds = new HashSet<>();
        for (Ordine ordine : listaOrdini) {
            if(!seenIds.contains(ordine.getOrder_id())) {
                modelloOrdine.addRow(new Object[]{
                        ordine.getOrder_id(), ordine.getDate(), ordine.getDelivery_status(), ordine.getPayment_status()
                });
                seenIds.add(ordine.getOrder_id());
            }
        }
    }

    public void mostraDettagliUtente(int userId, Container parent) {
        setContent(new UsersPanel(userId), parent);
    }

    public void mostraDettaglioProdotto(int prodottoId, Container parent) {
        setContent(new ProdottiPanel(prodottoId), parent);
    }

    public void mostraUserSelection(Container parent) {
        setContent(new UsersPanel(userId -> mostraCarrello(userId, parent, true)), parent);
    }

    public void mostraCarrello(int userId, Container parent, boolean isAdmin) {
        setContent(new CartPanel(userId, isAdmin), parent);
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
