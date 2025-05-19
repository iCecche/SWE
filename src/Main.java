import db.OrdineDAOImplementation;
import model.Ordine;
import model.User;
import db.UserDAOImplementation;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // USERDAO TEST
        OrdineDAOImplementation dao = new OrdineDAOImplementation();
        List<Ordine> rs = dao.searchAll();
        //rs.forEach(Ordine::print);

        List<Ordine> rs2 = dao.searchByUserID(1);
        //rs2.forEach(Ordine::print);

        List<Ordine> rs3 = dao.searchByProductID(2);
        rs3.forEach(Ordine::print);
    }
}