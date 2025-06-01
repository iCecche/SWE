package main.rowmapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class RowMapper<T> {
    public List<T> process(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    protected boolean contains(ResultSet rs, String column_name) throws SQLException {
        ResultSetMetaData rs_metadata = rs.getMetaData();
        int columns_count = rs_metadata.getColumnCount();
        for (int i = 1; i <= columns_count; i++) {
            if(Objects.equals(rs_metadata.getColumnName(i), column_name)) {
                return true;
            }
        }
        return false;
    }

    public abstract T mapRow(ResultSet rs) throws SQLException;
}
