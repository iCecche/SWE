package ui.panels;

import model.Prodotto;
import orm.OrdineDAOImplementation;
import orm.ProductDAOImplementation;
import model.Ordine;
import model.enums.DeliveryStatus;
import model.enums.PaymentStatus;
import services.OrderService;
import services.ProductService;
import ui.base.UIContext;
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

    public OrdiniPanel(UIContext uiContext) {
        super(uiContext);
    }

    @Override
    protected void initializeComponents() {
        this.orderService = new OrderService();
        this.productService = new ProductService();
        this.ordineMap = new HashMap<>();

        // Create table models with appropriate columns based on permissions
        String[] orderColumns = uiContext.getPermissionStrategy().getOrderTableColumns();
        String[] productColumns = uiContext.getPermissionStrategy().getProductTableColumns();

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

        if (uiContext.getPermissionStrategy().canCreateOrders()) {
            JButton addButton = new JButton("Aggiungi Ordine");
            addButton.addActionListener(this::handleAddOrder);
            buttonPanel.add(addButton);
        }

        if (uiContext.getPermissionStrategy().canDeleteOrders()) {
            JButton deleteButton = new JButton("Elimina");
            deleteButton.addActionListener(this::handleDeleteOrder);
            buttonPanel.add(deleteButton);

            JButton shipButton = new JButton("Spedisci");
            shipButton.addActionListener(this::handleShipOrder);
            buttonPanel.add(shipButton);
        }

        if (!uiContext.getPermissionStrategy().canDeleteOrders()) {
            JButton payButton = new JButton("Paga");
            payButton.addActionListener(this::handlePayOrder);
            buttonPanel.add(payButton);
        }
    }

    private void handleAddOrder(ActionEvent e) {
        if (uiContext.isAdmin()) {
            // Show user selection for admin
            showUserSelectionDialog();
        } else {
            // Direct to cart for regular user
            showCartPanel();
        }
    }

    private void handleDeleteOrder(ActionEvent e) {
        int orderId = getSelectedRowId(orderTable);
        if (orderId != -1 && confirmAction("Sei sicuro di voler eliminare questo ordine?")) {
            orderService.deleteOrder(orderId);
            loadData();
            showInfo("Ordine eliminato con successo.");
        }
    }

    private void handlePayOrder(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) return;

        int paymentStatusCol = getPaymentStatusColumnIndex();
        String paymentStatus = (String) orderTable.getValueAt(selectedRow, paymentStatusCol);

        if (PaymentStatus.fromString(paymentStatus) == PaymentStatus.PAID) {
            showError("L'ordine è già stato pagato.");
            return;
        }

        int orderId = getSelectedRowId(orderTable);
        if (orderId != -1) {
            orderService.updatePaymentStatus(orderId, PaymentStatus.PAID);
            loadData();
            showInfo("Pagamento effettuato con successo.");
        }
    }

    private void handleShipOrder(ActionEvent e) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) return;

        int deliveryStatusCol = getDeliveryStatusColumnIndex();
        String deliveryStatus = (String) orderTable.getValueAt(selectedRow, deliveryStatusCol);
        DeliveryStatus status = DeliveryStatus.fromString(deliveryStatus);

        if (status == DeliveryStatus.SHIPPED || status == DeliveryStatus.DELIVERED) {
            showError("L'ordine è già stato gestito.");
            return;
        }

        int paymentStatusCol = getPaymentStatusColumnIndex();
        String paymentStatus = (String) orderTable.getValueAt(selectedRow, paymentStatusCol);
        PaymentStatus payment = PaymentStatus.fromString(paymentStatus);

        if (payment != PaymentStatus.PAID) {
            showError("L'ordine deve prima essere pagato.");
        }

        int orderId = getSelectedRowId(orderTable);
        if (orderId != -1 && confirmAction("Confermi la spedizione dell'ordine?")) {
            orderService.updateDeliveryStatus(orderId, DeliveryStatus.SHIPPED);
            loadData();
            showInfo("Ordine spedito con successo.");
        }
    }

    private void showUserSelectionDialog() {
        Container parent = getParent();
        setContent(UsersPanel.createUserSelectionView(uiContext, userID -> showCartPanel(userID, parent)), parent);
    }

    private void showCartPanel() {
        Container parent = getParent();
        showCartPanel(uiContext.getUserId(), parent);
    }

    private void showCartPanel(int targetUserId, Container parent) {
        if (parent != null) {
            setContent(new CartPanel(uiContext, targetUserId), parent);
        }
    }

    private void showUserDetails(int userId) {
        Container parent = getParent();
        setContent(UsersPanel.createAdminProfileView(uiContext, userId), parent);
    }

    private int getPaymentStatusColumnIndex() {
        String[] columns = uiContext.getPermissionStrategy().getOrderTableColumns();
        for (int i = 0; i < columns.length; i++) {
            if ("Payment Status".equals(columns[i])) {
                return i;
            }
        }
        return -1;
    }

    private int getDeliveryStatusColumnIndex() {
        String[] columns = uiContext.getPermissionStrategy().getOrderTableColumns();
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

        List<Ordine> ordini = uiContext.getPermissionStrategy().canViewAllOrders()
                ? orderService.getOrders()
                : orderService.getOrdersByUserID(uiContext.getUserId());

        ordini.forEach(this::addOrderToTable);
    }

    private void addOrderToTable(Ordine ordine) {
        Object[] rowData;
        if (uiContext.isAdmin()) {
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
