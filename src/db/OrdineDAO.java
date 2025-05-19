package db;

import model.Ordine;
import java.util.List;

public interface OrdineDAO {

    abstract List<Ordine> search();
    abstract void insert();
    abstract void update();
    abstract void delete();

}
