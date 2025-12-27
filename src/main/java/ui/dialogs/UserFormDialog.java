package ui.dialogs;

import orm.UserDAOImplementation;
import model.User;
import model.enums.UserRole;
import services.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserFormDialog {

    public static void showAddDialog(Frame parent, UserService userService, Runnable onSuccess) {
        Map<String, Object> fields = createAddUserFields();

        new Dialog(parent, "Aggiungi Utente", fields, values -> {
            try {
                createUser(userService, values);
                showSuccess(parent, "Utente aggiunto con successo!");
                onSuccess.run();
            } catch (Exception e) {
                showError(parent, "Errore nell'aggiunta dell'utente: " + e.getMessage());
            }
        });
    }

    public static void showEditDialog(Frame parent, User user, UserService userService, Runnable onSuccess) {
        Map<String, Object> fields = createEditUserFields(user);

        new Dialog(parent, "Modifica Utente", fields, values -> {
            try {
                updateUser(userService, user.getId(), values);
                showSuccess(parent, "Utente modificato con successo!");
                onSuccess.run();
            } catch (Exception e) {
                showError(parent, "Errore nella modifica dell'utente: " + e.getMessage());
            }
        });
    }

    private static Map<String, Object> createAddUserFields() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("Username", "Inserisci username...");
        fields.put("Password", "Inserisci password...");
        fields.put("Nome", "Inserisci nome...");
        fields.put("Cognome", "Inserisci cognome...");
        fields.put("Role", "USER o ADMIN");
        return fields;
    }

    private static Map<String, Object> createEditUserFields(User user) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("Nome", user.getNome() != null ? user.getNome() : "");
        fields.put("Cognome", user.getCognome() != null ? user.getCognome() : "");
        fields.put("Indirizzo", user.getIndirizzo() != null ? user.getIndirizzo() : "");
        fields.put("Cap", user.getCap() != null ? user.getCap() : "");
        fields.put("Provincia", user.getProvincia() != null ? user.getProvincia() : "");
        fields.put("Stato", user.getStato() != null ? user.getStato() : "");
        return fields;
    }

    private static void createUser(UserService userService, Map<String, String> values) {
        String username = values.get("Username");
        String password = values.get("Password");
        String nome = values.get("Nome");
        String cognome = values.get("Cognome");
        UserRole role = UserRole.fromString(values.get("Role"));

        validateAddUserInput(username, password, nome, cognome, role);
        userService.createUser(username, password, role, nome, cognome);
    }

    private static void updateUser(UserService userService, int userId, Map<String, String> values) {
        String nome = values.get("Nome");
        String cognome = values.get("Cognome");
        String indirizzo = values.get("Indirizzo");
        String cap = values.get("Cap");
        String provincia = values.get("Provincia");
        String stato = values.get("Stato");

        userService.updateUserInfo(userId, nome, cognome, indirizzo, cap, provincia, stato);
    }

    private static void validateAddUserInput(String username, String password, String nome, String cognome, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username è obbligatorio");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password è obbligatoria");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome è obbligatorio");
        }
        if (cognome == null || cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Cognome è obbligatorio");
        }
        if (role == null) {
            throw new IllegalArgumentException("Ruolo non valido (USER o ADMIN)");
        }
    }

    private static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}