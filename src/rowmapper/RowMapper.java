package rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class RowMapper<T> {
    public List<T> process(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<T>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    public abstract T mapRow(ResultSet rs) throws SQLException;
}
