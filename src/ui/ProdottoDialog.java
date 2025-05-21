package ui;

import javax.swing.*;
import java.awt.*;
import model.Prodotto;

public class ProdottoDialog extends JDialog {
    private JTextField txtId, txtNome, txtQuantita, txtPrezzo, txtDescription;
    private boolean salvato = false;
    private Prodotto prodotto;

    public ProdottoDialog(Frame parent, Prodotto p) {
        super(parent, "Prodotto", true);
        setSize(300, 300);
        setLayout(new GridLayout(6, 2));

        txtId = new JTextField();
        txtNome = new JTextField();
        txtQuantita = new JTextField();
        txtPrezzo = new JTextField();
        txtDescription = new JTextField();

        add(new JLabel("ID:")); add(txtId);
        add(new JLabel("Nome:")); add(txtNome);
        add(new JLabel("Quantità:")); add(txtQuantita);
        add(new JLabel("Prezzo:")); add(txtPrezzo);
        add(new JLabel("Fornitore:")); add(txtDescription);

        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");

        add(btnSalva); add(btnAnnulla);

        if (p != null) {
            txtId.setText(Integer.toString(p.getId()));
            txtId.setEnabled(false); // ID non modificabile
            txtNome.setText(p.getName());
            txtQuantita.setText(String.valueOf(p.getStock()));
            txtPrezzo.setText(String.valueOf(p.getPrice()));
            txtDescription.setText(p.getDescription());
        }

        btnSalva.addActionListener(e -> {
            prodotto = new Prodotto(
                    Integer.parseInt(txtId.getText()),
                    txtNome.getText(),
                    txtDescription.getText(),
                    Integer.parseInt(txtPrezzo.getText()),
                    Integer.parseInt(txtQuantita.getText())
            );
            salvato = true;
            setVisible(false);
        });

        btnAnnulla.addActionListener(e -> setVisible(false));
    }

    public boolean isSalvato() {
        return salvato;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }
}
