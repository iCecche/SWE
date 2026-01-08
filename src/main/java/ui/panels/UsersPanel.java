package ui.panels;

import model.exceptions.UserServiceException;
import model.User;
import services.SessionManager;
import services.UserService;
import ui.base.BasePanel;
import ui.dialogs.UserFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class UsersPanel extends BasePanel {

    // Panel Modes
    public enum ViewMode {
        ADMIN_ALL_USERS,    // Admin viewing all users
        USER_PROFILE,       // User viewing own profile  
        ADMIN_PROFILE,      // Admin viewing specific user profile
        USER_SELECTION      // Admin selecting user for order creation
    }

    // Instance variables
    private final PanelConfiguration config;

    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserService userService;
    private static SessionManager manager = SessionManager.getInstance();

    // Factory methods for different use cases
    public static UsersPanel createAdminUsersView() {
        return new UsersPanel(new PanelConfiguration(
            ViewMode.ADMIN_ALL_USERS, 0, null
        ));
    }

    public static UsersPanel createUserProfileView() {
        return new UsersPanel(new PanelConfiguration(
            ViewMode.USER_PROFILE, manager.getCurrentUser().getId(), null
        ));
    }

    public static UsersPanel createAdminProfileView(int targetUserId) {
        return new UsersPanel(new PanelConfiguration(
            ViewMode.ADMIN_PROFILE, targetUserId, null
        ));
    }

    public static UsersPanel createUserSelectionView(Consumer<Integer> onUserSelected) {
        return new UsersPanel(new PanelConfiguration(
            ViewMode.USER_SELECTION, 0, onUserSelected
        ));
    }

    // Main constructor - now completely safe
    private UsersPanel(PanelConfiguration config) {
        super(true);
        this.config = config;
        initializeComponents();
        setupLayout();
        loadData();
    }

    @Override
    protected void initializeComponents() {
        this.userService = new UserService();

        // Now config is guaranteed to be initialized
        String[] columns = getAppropriateColumns();
        tableModel = createNonEditableTableModel(columns);
        userTable = new JTable(tableModel);

        configureTableBehavior();
        createButtons();
    }

    private String[] getAppropriateColumns() {
        return switch (config.viewMode) {
            case USER_SELECTION -> new String[]{"ID", "Username", "Nome", "Cognome"};
            case USER_PROFILE -> new String[]{"ID", "Username", "Nome", "Cognome", "Indirizzo", "Cap", "Provincia", "Stato"};
            default -> manager.getPermissions().getUserTableColumns();
        };
    }

    private void configureTableBehavior() {
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (config.viewMode == ViewMode.USER_SELECTION) {
            setupUserSelectionBehavior();
        }
    }

    private void setupUserSelectionBehavior() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    selectUser();
                }
            }
        });
    }

    private void selectUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleziona un utente dalla tabella.");
            return;
        }

        // Check if user is deleted (for admin columns that include deleted status)
        if (isUserDeleted(selectedRow)) {
            showError("Impossibile selezionare un utente eliminato.");
            return;
        }

        int userId = getSelectedUserId();
        if (config.userSelectionCallback != null) {
            config.userSelectionCallback.accept(userId);
        }
    }

    private boolean isUserDeleted(int row) {
        if (config.viewMode != ViewMode.USER_SELECTION) return false;

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if ("Deleted".equals(tableModel.getColumnName(i))) {
                return (boolean) tableModel.getValueAt(row, i);
            }
        }
        // In selection mode, we filter out deleted users during loading
        return false;
    }

    @Override
    protected void setupLayout() {
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        if (buttonPanel != null && shouldShowButtons()) {
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private boolean shouldShowButtons() {
        return config.viewMode != ViewMode.USER_SELECTION;
    }

    private void createButtons() {
        if (!shouldShowButtons()) return;

        buttonPanel = new JPanel();

        switch (config.viewMode) {
            case ADMIN_ALL_USERS -> createAdminButtons();
            case USER_PROFILE, ADMIN_PROFILE -> createProfileButtons();
        }
    }

    private void createAdminButtons() {
        addButton("Aggiungi", this::handleAdd);
        addButton("Modifica", this::handleModify);
        addButton("Elimina", this::handleDelete);
        addButton("Cambia Ruolo", this::handleRoleToggle);
    }

    private void createProfileButtons() {
        addButton("Modifica", this::handleModify);
        if (canDeleteProfile()) {
            addButton("Elimina Profilo", this::handleDelete);
        }
    }

    private boolean canDeleteProfile() {
        return config.viewMode == ViewMode.USER_PROFILE ||
                (config.viewMode == ViewMode.ADMIN_PROFILE && config.targetUserId != manager.getCurrentUser().getId());
    }

    private void addButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        buttonPanel.add(button);
    }

    // Event Handlers
    private void handleAdd() {
        UserFormDialog.showAddDialog(null, userService, this::loadData);
    }

    private void handleModify() {
        try {
            User user = getSelectedUser();
            UserFormDialog.showEditDialog(null, user, userService, this::loadData);
        }catch (UserServiceException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleDelete() {
        int user_id = getSelectedUserId();
        if (!confirmAction("Sei sicuro di voler eliminare l'utente")) return;

        try {
            userService.deleteUser(user_id);
            showInfo("Utente eliminato con successo!");
            if(isSelfDeletion(user_id))
                handleSelfDeletion();
        } catch (UserServiceException ex) {
            showError("Errore nell'eliminazione: " + ex.getMessage());
        }finally {
            loadData();
        }
    }

    private boolean isSelfDeletion(int user_id) {
        return SessionManager.getInstance().getCurrentUser().getId() == user_id;
    }

    private void handleSelfDeletion() {
        SwingUtilities.invokeLater(() -> {
            SessionManager.getInstance().logout();
            SwingUtilities.getWindowAncestor(this).dispose();
            new ui.panels.LoginPanel().setVisible(true);
        });
    }

    private void handleRoleToggle() {
        int user_id = getSelectedUserId();
        if (!confirmAction("Cambiare il ruolo?")) return;

        try {
            userService.updateRole(user_id);
            loadData();
            showInfo("Ruolo cambiato con successo!");
        } catch (Exception e) {
            showError("Errore nel cambio ruolo: " + e.getMessage());
        }
    }

    // Utility Methods
    private User getSelectedUser() {
        int userId = getSelectedUserId();
        User user = null;
        try {
            user = userService.getUserInfoById(userId);
        } catch (UserServiceException ex) {
            showError(ex.getMessage());
        }
        return user;
    }

    private int getSelectedUserId() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return -1;

        return (Integer) tableModel.getValueAt(selectedRow, 0);
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);

        try {
            List<User> users = getUsersToDisplay();
            users.forEach(this::addUserToTable);

            if (users.isEmpty()) {
                showNoUsersMessage();
            }
        } catch (Exception e) {
            showError("Errore nel caricamento dei dati: " + e.getMessage());
        }
    }

    private List<User> getUsersToDisplay() {
        return switch (config.viewMode) {
            case ADMIN_ALL_USERS -> userService.getUsersInfo();
            case USER_PROFILE, ADMIN_PROFILE -> {
                User user = userService.getUserInfoById(config.targetUserId);
                yield user != null ? List.of(user) : List.of();
            }
            case USER_SELECTION -> userService.getUsersInfo().stream()
                    .filter(user -> !user.isDeleted())
                    .toList();
        };
    }

    private void showNoUsersMessage() {
        String message = switch (config.viewMode) {
            case USER_SELECTION -> "Nessun utente disponibile per la selezione.";
            case USER_PROFILE, ADMIN_PROFILE -> "Utente non trovato.";
            default -> "Nessun utente presente nel sistema.";
        };
        showError(message);
    }

    private void addUserToTable(User user) {
        Object[] rowData = switch (config.viewMode) {
            case USER_SELECTION -> new Object[]{
                    user.getId(), user.getUsername(), user.getNome(), user.getCognome()
            };
            case USER_PROFILE -> new Object[]{
                    user.getId(), user.getUsername(), user.getNome(), user.getCognome(),
                    user.getIndirizzo(), user.getCap(), user.getProvincia(), user.getStato()
            };
            default -> new Object[]{
                    user.getId(), user.getUsername(), user.getNome(), user.getCognome(),
                    user.getIndirizzo(), user.getCap(), user.getProvincia(), user.getStato(),
                    user.getRole(), user.isDeleted()
            };
        };
        tableModel.addRow(rowData);
    }
}
