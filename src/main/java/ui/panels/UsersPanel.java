package ui.panels;

import orm.UserDAOImplementation;
import model.User;
import model.enums.UserRole;
import services.UserService;
import ui.base.BasePanel;
import ui.base.UIContext;
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

    // Factory methods for different use cases
    public static UsersPanel createAdminUsersView(UIContext uiContext) {
        return new UsersPanel(uiContext, new PanelConfiguration(
            ViewMode.ADMIN_ALL_USERS, 0, null
        ));
    }

    public static UsersPanel createUserProfileView(UIContext uiContext) {
        return new UsersPanel(uiContext, new PanelConfiguration(
            ViewMode.USER_PROFILE, uiContext.getUserId(), null
        ));
    }

    public static UsersPanel createAdminProfileView(UIContext uiContext, int targetUserId) {
        return new UsersPanel(uiContext, new PanelConfiguration(
            ViewMode.ADMIN_PROFILE, targetUserId, null
        ));
    }

    public static UsersPanel createUserSelectionView(UIContext uiContext, Consumer<Integer> onUserSelected) {
        return new UsersPanel(uiContext, new PanelConfiguration(
            ViewMode.USER_SELECTION, 0, onUserSelected
        ));
    }

    // Main constructor - now completely safe
    private UsersPanel(UIContext uiContext, PanelConfiguration config) {
        super(uiContext, true);
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
            default -> uiContext.getPermissionStrategy().getUserTableColumns();
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
                (config.viewMode == ViewMode.ADMIN_PROFILE && config.targetUserId != uiContext.getUserId());
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
        User user = getSelectedUser();
        if (user == null) return;
        if (user.isDeleted()) {
            showError("L'utente è eliminato.");
            return;
        }

        UserFormDialog.showEditDialog(null, user, userService, this::loadData);
    }

    private void handleDelete() {
        User user = getSelectedUser();
        if (user == null) return;

        if (user.isDeleted()) {
            showError("Utente già eliminato.");
            return;
        }

        if (user.getRole() == UserRole.ADMIN && isSelfDeletion(user)) {
            showError("Non puoi eliminare il tuo profilo.");
            return;
        }

        String message = String.format("Sei sicuro di voler eliminare l'utente '%s'?",
                user.getUsername());

        if (!confirmAction(message)) return;

        try {
            userService.deleteUser(user.getId());

            if (isSelfDeletion(user)) {
                handleSelfDeletion();
            } else {
                loadData();
                showInfo("Utente eliminato con successo!");
            }
        } catch (Exception e) {
            showError("Errore nell'eliminazione: " + e.getMessage());
        }
    }

    private boolean isSelfDeletion(User user) {
        return config.viewMode == ViewMode.USER_PROFILE && user.getId() == uiContext.getUserId();
    }

    private void handleSelfDeletion() {
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new ui.panels.LoginPanel().setVisible(true);
        });
    }

    private void handleRoleToggle() {
        User user = getSelectedUser();
        if (user == null) return;

        if (user.isDeleted()) {
            showError("L'utente è eliminato");
            return;
        }

        UserRole newRole = user.getRole() == UserRole.ADMIN ? UserRole.USER : UserRole.ADMIN;
        String message = String.format("Cambiare il ruolo di '%s' da %s a %s?",
                user.getUsername(), user.getRole(), newRole);

        if (!confirmAction(message)) return;

        try {
            userService.updateRole(user.getId(), newRole);
            loadData();
            showInfo("Ruolo cambiato con successo!");
        } catch (Exception e) {
            showError("Errore nel cambio ruolo: " + e.getMessage());
        }
    }

    // Utility Methods
    private User getSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleziona un utente dalla tabella.");
            return null;
        }

        int userId = getSelectedUserId();
        User user = userService.searchUserInfoById(userId);

        if (user == null) {
            showError("Utente non trovato.");
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
            case ADMIN_ALL_USERS -> userService.searchUsersInfo();
            case USER_PROFILE, ADMIN_PROFILE -> {
                User user = userService.searchUserInfoById(config.targetUserId);
                yield user != null ? List.of(user) : List.of();
            }
            case USER_SELECTION -> userService.searchUsersInfo().stream()
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
