package ui.base;

public class UserPermissionStrategy implements UIPermissionStrategy {
    private static final String[] ORDER_COLUMNS = {"Order ID", "Date", "Delivery Status", "Payment Status"};
    private static final String[] PRODUCT_COLUMNS = {"ID", "Nome", "Descrizione", "Prezzo", "Stock"};
    private static final String[] USER_COLUMNS = {"ID", "Username", "Nome", "Cognome", "Indirizzo", "Cap", "Provincia", "Stato"};

    @Override public boolean canCreateOrders() { return true; }
    @Override public boolean canModifyOrders() { return false; }
    @Override public boolean canDeleteOrders() { return false; }
    @Override public boolean canViewAllOrders() { return false; }
    @Override public boolean canManageUsers() { return false; }
    @Override public boolean canManageProducts() { return false; }
    @Override public boolean canViewAllUsers() { return false; }
    @Override public boolean isAdmin() { return false; }

    @Override public String[] getOrderTableColumns() { return ORDER_COLUMNS; }
    @Override public String[] getProductTableColumns() { return PRODUCT_COLUMNS; }
    @Override public String[] getUserTableColumns() { return USER_COLUMNS; }

}
