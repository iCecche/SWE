package db;

import model.QueryType;

import java.util.*;

public class QueryBuilder {

    private StringBuilder query;
    private List<Object> params;
    private record ColumnDef(String name, boolean isEnum, String enumType) {};
    private List<ColumnDef> columnDefList;
    private QueryType queryType;
    private boolean whereAdded;

    private QueryBuilder() {
        query = new StringBuilder();
        params = new ArrayList<>();
        columnDefList = new ArrayList<>();
        whereAdded = false;
        queryType = null;
    }

    public static QueryBuilder create() {
        return new QueryBuilder();
    }

    public QueryBuilder select(String... columns) {
        queryType = QueryType.SELECT;
        query.append("SELECT ");
        if(columns.length == 0) {
            query.append("*");
        }else {
            query.append(String.join(", ", columns));
        }
        return this;
    }

    public QueryBuilder insertInto(String table, String... columns) {
        queryType = QueryType.INSERT;
        query.append("INSERT INTO ").append(table);
        if(columns.length > 0) {
            query.append(" (")
                    .append(String.join(", ", columns))
                    .append(")");

            // Inizializza le definizioni delle colonne
            for (String col : columns) {
                columnDefList.add(new ColumnDef(col, false, null));
            }

        }
        return this;
    }

    // Metodo per registrare quali colonne sono enum
    public QueryBuilder withEnumColumn(String columnName, String enumType) {
        for (int i = 0; i < columnDefList.size(); i++) {
            if (columnDefList.get(i).name().equals(columnName)) {
                columnDefList.set(i, new ColumnDef(columnName, true, enumType));
                break;
            }
        }
        return this;
    }


    public QueryBuilder values(Object... params) {
        if (params.length != columnDefList.size()) {
            throw new IllegalArgumentException("Numero di valori non corrisponde al numero di colonne");
        }

        query.append(" VALUES (");
        StringJoiner joiner = new StringJoiner(", ");

        for (int i = 0; i < params.length; i++) {
            ColumnDef col = columnDefList.get(i);
            if (col.isEnum()) {
                joiner.add("?::" + col.enumType());
            } else {
                joiner.add("?");
            }
            this.params.add(params[i]);
        }

        query.append(joiner.toString()).append(")");
        return this;
    }



    public QueryBuilder update(String table) {
        queryType = QueryType.UPDATE;
        query.append("UPDATE ").append(table);
        return this;
    }

    public QueryBuilder set(String column, Object value, boolean isEnum) {
        if(!query.toString().contains("SET")) {
            query.append(" SET ");
        }else {
            query.append(", ");
        }
        if (isEnum) {
            query.append(column).append(" = ?::").append(column).append("_type");
        } else {
            query.append(column).append(" = ?");
        }

        params.add(value);
        return this;
    }

    public QueryBuilder set(String column, Object value) {
        return set(column, value, false);
    }

    public QueryBuilder deleteFrom(String table) {
        queryType = QueryType.DELETE;
        query.append("DELETE FROM ").append(table);
        return this;
    }

    public QueryBuilder join(String table, String condition) {
        query.append(" JOIN ").append(table).append(" ON ").append(condition);
        return this;
    }

    public QueryBuilder leftJoin(String table, String condition) {
        query.append(" LEFT JOIN ").append(table).append(" ON ").append(condition);
        return this;
    }

    // GROUP BY and HAVING
    public QueryBuilder groupBy(String... columns) {
        query.append(" GROUP BY ").append(String.join(", ", columns));
        return this;
    }

    public QueryBuilder having(String condition) {
        query.append(" HAVING ").append(condition);
        return this;
    }

    public QueryBuilder from(String table) {
        query.append(" FROM ").append(table);
        return this;
    }

    public QueryBuilder where(String condition) {
        query.append(whereAdded ? " AND " : " WHERE ");
        query.append(condition);
        whereAdded = true;
        return this;
    }

    public void addParameter(Object value) {
        params.add(value);
    }

    public QueryBuilder orderBy(String... columns) {
        query.append(" ORDER BY ").append(String.join(", ", columns));
        return this;
    }

    public Object[] getParameters() {
        return params.toArray();
    }

    // Validazione
    private void validateState() {
        if (queryType == null) {
            throw new IllegalStateException("Query type not specified");
        }
        if (query.isEmpty()) {
            throw new IllegalStateException("Empty query");
        }
    }

    public String getQuery() {
        validateState();
        return query.toString();
    }

}
