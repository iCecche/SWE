package ui;

import db.ProductDAOImplementation;
import model.Prodotto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProdottiPanel extends JPanel {

    private final boolean isAdmin;
    private JPanel panelBottoni;
    private JTable tabella;
    private DefaultTableModel modello;
    private ProductDAOImplementation prodottoDAO;

    public ProdottiPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.prodottoDAO = new ProductDAOImplementation();

        configurePanelLayout();

        modello = createTableModel();
        tabella = new JTable(modello);
        add(new JScrollPane(tabella), BorderLayout.CENTER);

        setupButtons();
        loadData();
    }

    private boolean isAdminView() {
        return isAdmin;
    }

    private void createButtons() {
        // Pulsanti
        panelBottoni = new JPanel();

        JButton btnAggiungi = new JButton("Aggiungi");
        JButton btnModifica = new JButton("Modifica");
        JButton btnElimina = new JButton("Elimina");

        panelBottoni.add(btnAggiungi);
        panelBottoni.add(btnModifica);
        panelBottoni.add(btnElimina);

        // Eventi
        btnAggiungi.addActionListener(this::onAdd);
        btnModifica.addActionListener(this::onModify);
        btnElimina.addActionListener(this::onDelete);
    }

    private void onAdd(ActionEvent e) {
        Map<String, Object> campi = getCampiDialog(null);

        new Dialog(null, "Aggiungi Prodotto", campi, valoriInseriti -> {
            Prodotto nuovoProdotto = getProdottoFromInput(valoriInseriti);
            prodottoDAO.insertNewProduct(
                    nuovoProdotto.getName(),
                    nuovoProdotto.getDescription(),
                    nuovoProdotto.getPrice(),
                    nuovoProdotto.getStock()
            );
            loadData();
        });
    }

    private void onModify(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            mostraErrore("Seleziona un prodotto.");
            return;
        }

        int id = getSelectedOrderId();
        Prodotto prod = prodottoDAO.searchById(id);

        if (prod == null) {
            mostraErrore("Prodotto non trovato.");
            return;
        }

        Map<String, Object> campi = getCampiDialog(prod);

        new Dialog(null, "Modifica Prodotto", campi, valoriInseriti -> {
            Prodotto aggiornato = getProdottoFromInput(valoriInseriti);
            prodottoDAO.updateProduct(id,
                    aggiornato.getName(),
                    aggiornato.getDescription(),
                    aggiornato.getPrice(),
                    aggiornato.getStock()
            );
            loadData();
        });
    }

    private void onDelete(ActionEvent e) {
        int selected = tabella.getSelectedRow();
        if (selected == -1) {
            mostraErrore("Seleziona un prodotto.");
            return;
        }

        // evita chiamata a db per prodotti già eliminati
        if(!getSelectedOrderDeleted())
            return;

        int id = getSelectedOrderId();
        prodottoDAO.deleteProduct(id);
        loadData();
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

    private void configurePanelLayout() {
        setSize(800, 500);
        setLayout(new BorderLayout());
    }

    private void setupButtons() {
        if(isAdminView()) {
            createButtons();
            add(panelBottoni, BorderLayout.SOUTH);
        }
    }

    private DefaultTableModel createTableModel() {
        String[] columns;
        if (isAdmin) columns = new String[]{"ID", "Nome", "Descrizione", "Prezzo", "Stock", "Deleted"};
        else columns = new String[]{"ID", "Nome", "Descrizione", "Prezzo", "Stock"};

        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void addOrderToTable(Prodotto prodotto) {
        Object[] rowData;
        if (isAdmin)
            rowData = new Object[]{prodotto.getId(), prodotto.getName(), prodotto.getDescription(), prodotto.getPrice(), prodotto.getStock(), prodotto.isDeleted()};
        else {
            if (prodotto.isDeleted())
                return;
            rowData = new Object[]{prodotto.getId(), prodotto.getName(), prodotto.getDescription(), prodotto.getPrice(), prodotto.getStock()};
        }
        modello.addRow(rowData);
    }

    private void loadData() {
        modello.setRowCount(0);

        List<Prodotto> prodotti = prodottoDAO.searchAll();
        prodotti.forEach(this::addOrderToTable);
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio);
    }

    private int getSelectedOrderId() {
        return Integer.parseInt(tabella.getValueAt(tabella.getSelectedRow(), 0).toString());
    }

    private boolean getSelectedOrderDeleted() {
        return (boolean) tabella.getValueAt(tabella.getSelectedRow(), 5);
    }
}