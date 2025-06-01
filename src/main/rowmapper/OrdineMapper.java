package main.rowmapper;

import main.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdineMapper extends RowMapper<Ordine> {
    private final Map<Integer, Ordine> ordiniMap = new HashMap<>();

    @Override
    public Ordine mapRow(ResultSet rs) throws SQLException {
        int orderId = rs.getInt("order_id");

        // Recupera l'ordine esistente o ne crea uno nuovo
        Ordine ordine = ordiniMap.computeIfAbsent(orderId, id -> costruisciOrdineBase(rs));

        // Aggiungi i dettagli se presenti
        aggiungiDettagliOrdine(ordine, rs);

        return ordine;
    }

    private Ordine costruisciOrdineBase(ResultSet rs) {
        try {
            OrdineBuilder builder = OrdineBuilder.create();
            builder.withOrderId(rs.getInt("order_id"));

            if (contains(rs, "user_id")) {
                builder.withUserId(rs.getInt("user_id"));
            }
            if (contains(rs, "date")) {
                builder.withDate(rs.getDate("date"));
            }
            if (contains(rs, "payment_status")) {
                PaymentStatus payment_status = PaymentStatus.fromString(rs.getString("payment_status"));
                builder.withPaymentStatus(payment_status);
            }
            if (contains(rs, "delivery_status")) {
                DeliveryStatus delivery_status = DeliveryStatus.fromString(rs.getString("delivery_status"));
                builder.withDeliveryStatus(delivery_status);
            }

            return builder.build();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la costruzione dell'ordine", e);
        }
    }

    private void aggiungiDettagliOrdine(Ordine ordine, ResultSet rs) throws SQLException {
        if (contains(rs, "product_id") && contains(rs, "quantity")) {
            int productId = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");

            DettaglioOrdine dettaglio = new DettaglioOrdine();
            dettaglio.setProduct_id(productId);
            dettaglio.setQuantity(quantity);

            ordine.setDetails(dettaglio);
        }
    }

    @Override
    public List<Ordine> process(ResultSet rs) throws SQLException {
        ordiniMap.clear();
        while (rs.next()) {
            mapRow(rs);
        }
        return new ArrayList<>(ordiniMap.values());
    }

}