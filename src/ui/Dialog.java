package ui;

import model.Prodotto;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Dialog  extends JDialog {

    public Dialog(Frame parent, String title, Map<String, Object> campi, Consumer<Map<String, String>> onSave) {
        super(parent, title, true);

        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Map<String, JTextField> fieldMap = new HashMap<>();
        int row = 0;

        for (Map.Entry<String, Object> entry : campi.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel(key + ":"), gbc);

            gbc.gridx = 1;
            JTextField field = new JTextField(20);
            field.setText(value != null ? value.toString() : "");
            formPanel.add(field, gbc);

            fieldMap.put(key, field);
            row++;
        }

        // Pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton salvaButton = new JButton("Salva");
        JButton annullaButton = new JButton("Annulla");

        salvaButton.addActionListener(e -> {
            Map<String, String> valoriInseriti = new HashMap<>();
            for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
                valoriInseriti.put(entry.getKey(), entry.getValue().getText());
            }
            dialog.dispose();
            onSave.accept(valoriInseriti); // callback
        });

        annullaButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(annullaButton);
        buttonPanel.add(salvaButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
