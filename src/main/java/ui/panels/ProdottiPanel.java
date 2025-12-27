package ui.panels;

import orm.ProductDAOImplementation;
import model.Prodotto;
import services.ProductService;
import ui.dialogs.Dialog;
import ui.base.BasePanel;
import ui.base.UIContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProdottiPanel extends BasePanel {
    private JTable tabella;
    private DefaultTableModel modello;
    private ProductService productService;

    public ProdottiPanel(UIContext uiContext) {
        super(uiContext);
    }

    @Override
    protected void initializeComponents() {
        this.productService = new ProductService();

        String[] columns = uiContext.getPermissionStrategy().getProductTableColumns();
        modello = createNonEditableTableModel(columns);
        tabella = new JTable(modello);

        if (uiContext.getPermissionStrategy().canManageProducts()) {
            createButtons();
        }
    }

    @Override
    protected void setupLayout() {
        add(new JScrollPane(tabella), BorderLayout.CENTER);
        if (buttonPanel != null) {
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void createButtons() {
        buttonPanel = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnModifica = new JButton("Modifica");
        JButton btnElimina = new JButton("Elimina");

        btnAggiungi.addActionListener(this::onAdd);
        btnModifica.addActionListener(this::onModify);
        btnElimina.addActionListener(this::onDelete);

        buttonPanel.add(btnAggiungi);
        buttonPanel.add(btnModifica);
        buttonPanel.add(btnElimina);
    }

    private void onAdd(ActionEvent e) {
        Map<String, Object> campi = getCampiDialog(null);

        new Dialog(null, "Aggiungi Prodotto", campi, valoriInseriti -> {
            try {
                Prodotto nuovoProdotto = getProdottoFromInput(valoriInseriti);
                productService.insertNewProduct(
                        nuovoProdotto.getName(),
                        nuovoProdotto.getDescription(),
                        nuovoProdotto.getPrice(),
                        nuovoProdotto.getStock()
                );
                loadData();
                showInfo("Prodotto aggiunto con successo!");
            } catch (Exception ex) {
                showError("Errore nell'aggiunta del prodotto: " + ex.getMessage());
            }
        });
    }

    private void onModify(ActionEvent e) {
        int id = getSelectedRowId(tabella);
        if (id == -1) return;

        Prodotto prod = productService.getProductById(id);
        if (prod == null) {
            showError("Prodotto non trovato.");
            return;
        }

        Map<String, Object> campi = getCampiDialog(prod);

        new Dialog(null, "Modifica Prodotto", campi, valoriInseriti -> {
            try {
                Prodotto aggiornato = getProdottoFromInput(valoriInseriti);
                productService.updateProduct(id,
                        aggiornato.getName(),
                        aggiornato.getDescription(),
                        aggiornato.getPrice(),
                        aggiornato.getStock()
                );
                loadData();
                showInfo("Prodotto modificato con successo!");
            } catch (Exception ex) {
                showError("Errore nella modifica del prodotto: " + ex.getMessage());
            }
        });
    }

    private void onDelete(ActionEvent e) {
        int id = getSelectedRowId(tabella);
        if (id == -1) return;

        int selectedRow = tabella.getSelectedRow();
        if (uiContext.isAdmin()) {
            // Check if already deleted for admin view
            boolean isDeleted = (Boolean) tabella.getValueAt(selectedRow, 5);
            if (isDeleted) {
                showError("Prodotto già eliminato.");
                return;
            }
        }

        if (confirmAction("Sei sicuro di voler eliminare questo prodotto?")) {
            productService.deleteProduct(id);
            loadData();
            showInfo("Prodotto eliminato con successo!");
        }
    }

    private Map<String, Object> getCampiDialog(Prodotto prod) {
        Map<String, Object> campi = new LinkedHashMap<>();
        campi.put("Nome", prod != null ? prod.getName() : "Nome del prodotto...");
        campi.put("Descrizione", prod != null ? prod.getDescription() : "Descrizione del prodotto...");
        campi.put("Prezzo", prod != null ? String.valueOf(prod.getPrice()) : "Prezzo del prodotto...");
        campi.put("Quantità", prod != null ? String.valueOf(prod.getStock()) : "Quantità del prodotto...");
        return campi;
    }

    private Prodotto getProdottoFromInput(Map<String, String> input) {
        String nome = input.get("Nome");
        String descrizione = input.get("Descrizione");
        int prezzo = Integer.parseInt(input.get("Prezzo"));
        int quantità = Integer.parseInt(input.get("Quantità"));

        return new Prodotto(0, nome, descrizione, prezzo, quantità, false);
    }

    @Override
    protected void loadData() {
        modello.setRowCount(0);

        List<Prodotto> prodotti = productService.getAllProducts();
        prodotti.forEach(this::addProductToTable);
    }

    private void addProductToTable(Prodotto prodotto) {
        Object[] rowData;
        if (uiContext.isAdmin()) {
            rowData = new Object[]{
                    prodotto.getId(), prodotto.getName(), prodotto.getDescription(),
                    prodotto.getPrice(), prodotto.getStock(), prodotto.isDeleted()
            };
        } else {
            if (prodotto.isDeleted()) return; // Skip deleted products for users
            rowData = new Object[]{
                    prodotto.getId(), prodotto.getName(), prodotto.getDescription(),
                    prodotto.getPrice(), prodotto.getStock()
            };
        }
        modello.addRow(rowData);
    }
}