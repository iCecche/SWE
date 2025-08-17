package ui.base;

import ui.panels.PanelConfiguration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    protected final UIContext uiContext;
    protected JPanel buttonPanel;

    protected BasePanel(UIContext uiContext) {
        this.uiContext = uiContext;
        setupPanel();
        initializeComponents();
        setupLayout();
        loadData();
    }

    protected BasePanel(UIContext uiContext, boolean isCustomConfig) {
        this.uiContext = uiContext;
        setupPanel();
    }

    private void setupPanel() {
        setSize(800, 500);
        setLayout(new BorderLayout());
    }

    protected abstract void initializeComponents();
    protected abstract void setupLayout();
    protected abstract void loadData();

    protected DefaultTableModel createNonEditableTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean confirmAction(String message) {
        return JOptionPane.showConfirmDialog(this, message,
                "Conferma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    protected int getSelectedRowId(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleziona una riga dalla tabella.");
            return -1;
        }
        return Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
    }

    protected void setContent(Component comp, Container parent) {
        parent.removeAll();
        parent.add(comp, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }
}
