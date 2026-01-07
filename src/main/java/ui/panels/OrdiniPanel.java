package ui.panels;

import model.Prodotto;
import model.exceptions.OrderBusinessException;
import model.Ordine;
import services.OrderService;
import services.ProductService;
import services.SessionManager;
import ui.base.BasePanel;

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

public class OrdiniPanel extends BasePanel {
    private Map<Integer, Ordine> ordineMap;
    private JTable orderTable;
    private JTable detailTable;
    private DefaultTableModel orderModel;
    private DefaultTableModel detailModel;
    private OrderService orderService;
    private ProductService productService;
    private SessionManager manager;

    public OrdiniPanel() {
        super();
    }

    @Override
    protected void initializeComponents() {
        this.orderService = new OrderService();
        this.productService = new ProductService();
        this.manager = SessionManager.getInstance();
        this.ordineMap = new HashMap<>();

        // Create table models with appropriate columns based on permissions
        String[] orderColumns = manager.getPermissions().getOrderTableColumns();
        String[] productColumns = manager.getPermissions().getProductTableColumns();

        orderModel = createNonEditableTableModel(orderColumns);
        detailModel = createNonEditableTableModel(productColumns);

        orderTable = new JTable(orderModel);
        detailTable = new JTable(detailModel);

        setupOrderTableSelection();
        createButtons();
    }

    private void setupOrderTableSelection() {
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(e -> handleOrderSelection(e, orderTable));
        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    handleUserIdDoubleClick(orderTable, e);
                }
            }
        });
    }

    private void handleOrderSelection(ListSelectionEvent e, JTable table) {
        if (e.getValueIsAdjusting() || table.getSelectedRow() == -1) {
            return;
        }

        updateDetailTable();
    }

    private void handleUserIdDoubleClick(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (table.getColumnName(col).equals("User ID")) {
            Object value = table.getValueAt(row, col);
            if (value != null) {
                int userId = Integer.parseInt(value.toString());
                showUserDetails(userId);
            }
        }
    }

    private void updateDetailTable() {
        detailModel.setRowCount(0);
        int orderId = getSelectedRowId(orderTable);
        if (orderId != -1) {
            Ordine ordine = ordineMap.get(orderId);
            if (ordine != null) {
                ordine.getDetails().forEach(detail -> {
                    Prodotto prodotto = productService.getProductById(detail.getProduct_id());
                    detailModel.addRow(new Object[]{
                            prodotto.getId(), prodotto.getName(), prodotto.getDescription(),
                            prodotto.getPrice(), detail.getQuantity(), prodotto.isDeleted()
                    });
                });
            }
        }
    }

    @Override
    protected void setupLayout() {
        JPanel splitPane = new JPanel(new GridLayout(1, 2, 0, 0));
        splitPane.add(new JScrollPane(orderTable));
        splitPane.add(new JScrollPane(detailTable));

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        add(container, BorderLayout.CENTER);
    }

    private void createButtons() {
        buttonPanel = new JPanel();

        if (manager.getPermissions().canCreateOrders()) {
            JButton addButton = new JButton("Aggiungi Ordine");
            addButton.addActionListener(this::handleAddOrder);
            buttonPanel.add(addButton);
        }

        if (manager.getPermissions().canDeleteOrders()) {
            JButton deleteButton = new JButton("Elimina");
            deleteButton.addActionListener(this::handleDeleteOrder);
            buttonPanel.add(deleteButton);

            JButton shipButton = new JButton("Spedisci");
            shipButton.addActionListener(this::handleShipOrder);
            buttonPanel.add(shipButton);
        }

        if (!manager.getPermissions().canDeleteOrders()) {
            JButton payButton = new JButton("Paga");
            payButton.addActionListener(this::handlePayOrder);
            buttonPanel.add(payButton);
        }
    }

    private void handleAddOrder(ActionEvent e) {
        if (manager.isAdmin()) {
            // Show user selection for admin
            showUserSelectionDialog();
        } else {
            // Direct to cart for regular user
            showCartPanel();
        }
    }

    private void handleDeleteOrder(ActionEvent e) {
        int orderId = getSelectedRowId(orderTable);
        if (orderId == -1) return;
        if (confirmAction("Sei sicuro di voler eliminare questo ordine?")) {
            orderService.deleteOrder(orderId);
            loadData();
            showInfo("Ordine eliminato con successo.");
        }
    }

    private void handlePayOrder(ActionEvent e) {
        int orderId = getSelectedRowId(orderTable);
        if (orderId == -1) return;
        try {
            orderService.payOrder(orderId);
            showInfo("Ordine pagato con successo.");
        }catch (OrderBusinessException ex) {
            showError(ex.getMessage());
        }finally {
            loadData();
        }
    }

    private void handleShipOrder(ActionEvent e) {
        int orderId = getSelectedRowId(orderTable);

        try {
            if (confirmAction("Confermi la spedizione dell'ordine?")) {
                orderService.shipOrder(orderId);
            }
            showInfo("Ordine spedito con successo.");
        }catch (OrderBusinessException ex) {
            showError(ex.getMessage());
        }finally {
            loadData();
        }
    }

    private void showUserSelectionDialog() {
        Container parent = getParent();
        setContent(UsersPanel.createUserSelectionView(userID -> showCartPanel(userID, parent)), parent);
    }

    private void showCartPanel() {
        Container parent = getParent();
        showCartPanel(manager.getCurrentUser().getId(), parent);
    }

    private void showCartPanel(int targetUserId, Container parent) {
        if (parent != null) {
            setContent(new CartPanel(targetUserId), parent);
        }
    }

    private void showUserDetails(int userId) {
        Container parent = getParent();
        setContent(UsersPanel.createAdminProfileView(userId), parent);
    }

    private int getPaymentStatusColumnIndex() {
        String[] columns = manager.getPermissions().getOrderTableColumns();
        for (int i = 0; i < columns.length; i++) {
            if ("Payment Status".equals(columns[i])) {
                return i;
            }
        }
        return -1;
    }

    private int getDeliveryStatusColumnIndex() {
        String[] columns = manager.getPermissions().getOrderTableColumns();
        for (int i = 0; i < columns.length; i++) {
            if ("Delivery Status".equals(columns[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void loadData() {
        orderModel.setRowCount(0);
        detailModel.setRowCount(0);
        ordineMap.clear();

        List<Ordine> ordini = manager.getPermissions().canViewAllOrders()
                ? orderService.getOrders()
                : orderService.getOrdersByUserID(manager.getCurrentUser().getId());

        ordini.forEach(this::addOrderToTable);
    }

    private void addOrderToTable(Ordine ordine) {
        Object[] rowData;
        if (manager.isAdmin()) {
            rowData = new Object[]{
                    ordine.getOrder_id(), ordine.getUser_id(), ordine.getDate(),
                    ordine.getDelivery_status(), ordine.getPayment_status()
            };
        } else {
            rowData = new Object[]{
                    ordine.getOrder_id(), ordine.getDate(),
                    ordine.getDelivery_status(), ordine.getPayment_status()
            };
        }

        orderModel.addRow(rowData);
        ordineMap.put(ordine.getOrder_id(), ordine);
    }
}
