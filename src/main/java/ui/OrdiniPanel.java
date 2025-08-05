package ui;

import db.OrdineDAOImplementation;
import db.ProductDAOImplementation;
import model.Ordine;
import model.Prodotto;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdiniPanel extends JPanel {

    private static final String[] ADMIN_COLUMNS = {"Order ID", "User ID", "Date", "Delivery Status", "Payment Status"};
    private static final String[] USER_COLUMNS = {"Order ID", "Date", "Delivery Status", "Payment Status"};
    private static final String[] DETAIL_COLUMNS = {"Product ID", "Nome", "Descrizione", "Prezzo", "Quantity"};

    private final Map<Integer, Ordine> ordineMap = new HashMap<>();
    private JPanel panelBottoni;
    private final JTable dettaglioTable;
    private final JTable orderTable;
    private final DefaultTableModel modelloDettaglio;
    private final DefaultTableModel modelloOrdine;
    private final OrdineDAOImplementation ordineDAO;
    private final ProductDAOImplementation prodottoDAO;
    private final Integer userId;

    public OrdiniPanel() {
        this(null);
    }

    public OrdiniPanel(Integer userId) {
        this.userId = userId;
        this.ordineDAO = new OrdineDAOImplementation();
        this.prodottoDAO = new ProductDAOImplementation();

        configurePanelLayout();

        modelloOrdine = createTableModel(isAdminView() ? ADMIN_COLUMNS : USER_COLUMNS);
        modelloDettaglio = createTableModel(DETAIL_COLUMNS);

        orderTable = createOrderTable();
        dettaglioTable = new JTable(modelloDettaglio);

        setupTableLayout();
        setupButtons();
        loadData();
    }

    private boolean isAdminView() {
        return userId == null;
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createOrderTable() {
        JTable table = new JTable(modelloOrdine);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> handleOrderSelection(e, table));

        if (isAdminView()) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                        handleUserIdDoubleClick(table, e);
                    }
                }
            });
        }

        return table;
    }

    private void handleOrderSelection(ListSelectionEvent e, JTable table) {
        if (e.getValueIsAdjusting() || table.getSelectedRow() == -1) {
            return;
        }

        updateDetailTable(getSelectedOrderId(table));
    }

    private void updateDetailTable(int orderId) {
        modelloDettaglio.setRowCount(0);
        Ordine ordine = ordineMap.get(orderId);

        if (ordine != null) {
            ordine.getDetails().forEach(detail -> {
                Prodotto prodotto = prodottoDAO.searchById(detail.getProduct_id());
                modelloDettaglio.addRow(new Object[]{
                        prodotto.getId(), prodotto.getName(),
                        prodotto.getDescription(), prodotto.getPrice(),
                        detail.getQuantity()
                });
            });
        }
    }

    private void setupTableLayout() {
        JPanel splitPane = new JPanel(new GridLayout(1, 2, 0, 0));
        splitPane.add(new JScrollPane(orderTable));
        splitPane.add(new JScrollPane(dettaglioTable));

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private void setupButtons() {
        panelBottoni = new JPanel();
        if (isAdminView()) {
            createAdminButtons();
        } else {
            createUserButtons();
        }
        add(panelBottoni, BorderLayout.SOUTH);
    }

    private void loadData() {
        modelloOrdine.setRowCount(0);
        modelloDettaglio.setRowCount(0);

        List<Ordine> ordini = isAdminView() ?
                ordineDAO.searchAll() :
                ordineDAO.searchByUserID(userId);

        ordini.forEach(this::addOrderToTable);
    }

    private void addOrderToTable(Ordine ordine) {
        Object[] rowData = isAdminView() ?
                new Object[]{ordine.getOrder_id(), ordine.getUser_id(),
                        ordine.getDate(), ordine.getDelivery_status(),
                        ordine.getPayment_status()} :
                new Object[]{ordine.getOrder_id(), ordine.getDate(),
                        ordine.getDelivery_status(), ordine.getPayment_status()};

        modelloOrdine.addRow(rowData);
        ordineMap.put(ordine.getOrder_id(), ordine);
    }

    private void configurePanelLayout() {
        setSize(800, 500);
        setLayout(new BorderLayout());
    }

    private void handleUserIdDoubleClick(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (table.getColumnName(col).equals("User ID")) {
            Object value = table.getValueAt(row, col);
            if (value != null) {
                int userId = Integer.parseInt(value.toString());
                mostraDettagliUtente(userId, getParent());
            }
        }
    }

    private int getSelectedOrderId(JTable table) {
        return Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
    }

    private void createAdminButtons() {
        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnElimina = new JButton("Elimina");
        JButton btnSpedisci = new JButton("Spedisci");

        btnAggiungi.addActionListener(this::handleAdminAdd);
        btnElimina.addActionListener(this::handleDelete);
        btnSpedisci.addActionListener(this::handleShip);

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnElimina);
        panelBottoni.add(btnSpedisci);
    }

    private void createUserButtons() {
        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnPaga = new JButton("Paga");

        btnAggiungi.addActionListener(this::handleUserAdd);
        btnPaga.addActionListener(this::handlePayment);

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnPaga);
    }

    private void handleAdminAdd(ActionEvent e) {
        mostraUserSelection(getParent());
    }

    private void handleUserAdd(ActionEvent e) {
        mostraCarrello(userId, getParent(), false);
    }

    private void handleDelete(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            mostraErrore("Seleziona un ordine.");
            return;
        }

        int orderId = getSelectedOrderId(orderTable);
        ordineDAO.deleteOrder(orderId);
        loadData();
    }

    private void handlePayment(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            mostraErrore("Seleziona un ordine.");
            return;
        }

        int paymentStatusCol = orderTable.getColumn("Payment Status").getModelIndex();
        String paymentStatus = (String) orderTable.getValueAt(selectedRow, paymentStatusCol);

        if (PaymentStatus.fromString(paymentStatus) == PaymentStatus.PAID) {
            mostraErrore("L'ordine è già stato pagato.");
            return;
        }

        int orderId = getSelectedOrderId(orderTable);
        ordineDAO.updatePaymentStatus(orderId, PaymentStatus.PAID);
        System.out.println("L'ordine pagato con successo.");
        loadData();
    }

    private void handleShip(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            mostraErrore("Seleziona un ordine.");
            return;
        }

        int deliveryStatusCol = orderTable.getColumn("Delivery Status").getModelIndex();
        String deliveryStatus = (String) orderTable.getValueAt(selectedRow, deliveryStatusCol);
        DeliveryStatus status = DeliveryStatus.fromString(deliveryStatus);

        if (status == DeliveryStatus.SHIPPED || status == DeliveryStatus.DELIVERED) {
            mostraErrore("L'ordine è già stato gestito.");
            return;
        }

        int orderId = getSelectedOrderId(orderTable);
        ordineDAO.updateDeliveryStatus(orderId, DeliveryStatus.SHIPPED);
        System.out.println("L'ordine è stato gestito con successo.");
        loadData();
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio);
    }

    public void mostraDettagliUtente(int userId, Container parent) {
        setContent(new UsersPanel(userId), parent);
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
}
