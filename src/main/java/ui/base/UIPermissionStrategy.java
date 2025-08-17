package ui.base;

public interface UIPermissionStrategy {
    boolean canCreateOrders();
    boolean canModifyOrders();
    boolean canDeleteOrders();
    boolean canViewAllOrders();
    boolean canManageUsers();
    boolean canManageProducts();
    boolean canViewAllUsers();
    boolean isAdmin();

    String[] getOrderTableColumns();
    String[] getProductTableColumns();
    String[] getUserTableColumns();

}
