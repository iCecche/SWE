package ui.base;

public class AdminPermissionStrategy implements UIPermissionStrategy {
    private static final String[] ORDER_COLUMNS = {"Order ID", "User ID", "Date", "Delivery Status", "Payment Status"};
    private static final String[] PRODUCT_COLUMNS = {"ID", "Nome", "Descrizione", "Prezzo", "Stock", "Deleted"};
    private static final String[] USER_COLUMNS = {"ID", "Username", "Nome", "Cognome", "Indirizzo", "Cap", "Provincia", "Stato", "Role", "Deleted"};

    @Override public boolean canCreateOrders() { return true; }
    @Override public boolean canModifyOrders() { return true; }
    @Override public boolean canDeleteOrders() { return true; }
    @Override public boolean canViewAllOrders() { return true; }
    @Override public boolean canManageUsers() { return true; }
    @Override public boolean canManageProducts() { return true; }
    @Override public boolean canViewAllUsers() { return true; }
    @Override public boolean isAdmin() { return true; }

    @Override public String[] getOrderTableColumns() { return ORDER_COLUMNS; }
    @Override public String[] getProductTableColumns() { return PRODUCT_COLUMNS; }
    @Override public String[] getUserTableColumns() { return USER_COLUMNS; }

}
