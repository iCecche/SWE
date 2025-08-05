package db;

import rowmapper.RowMapper;

import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DBManager {

    private static final AtomicReference<DBManager> instance = new AtomicReference<>();
    private Connection connection;

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String driverClassName;

    private final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();
    private final ThreadLocal<Boolean> inTransaction = new ThreadLocal<>();

    // singleton pattern for a single connection to db
    private DBManager() {
        this.jdbcUrl = System.getenv("JDBC_URL");
        this.username = System.getenv("JDBC_USERNAME");
        this.password = System.getenv("JDBC_PASSWORD");
        this.driverClassName = System.getenv("JDBC_DRIVER_CLASS_NAME");
        this.inTransaction.set(false);
    };

    protected DBManager(String jdbcUrl, String username, String password, String driverClassName) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
        this.inTransaction.set(false);
    }

    public static DBManager getTestInstance(String jdbcUrl, String username, String password, String driverClassName) {
        return new DBManager(jdbcUrl, username, password, driverClassName);
    }

    private static void resetInstance() {
        instance.set(null);
    }

    public static DBManager getInstance() throws SQLException {
        return instance.updateAndGet(dbManager -> dbManager != null ? dbManager : new DBManager());
    }

    public Connection getConnection() {
        return connection;
    }

    private void connect() {
        try {
            // Connect to the database
            Class.forName(driverClassName);
            if(Boolean.TRUE.equals(inTransaction.get()))
                connection = transactionConnection.get();
            else
                connection = DriverManager.getConnection(jdbcUrl, username, password);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T execute_transaction(TransactionCallback<T> callback) {
        try {
            beginTransaction();
            T result = callback.execute();
            commitTransaction();
            return result;
        }catch (Exception e) {
            rollbackTransaction();
            throw new RuntimeException("Errore durante l'esecuzione della transazione", e);
        } finally {
            endTransaction();
        }
    }

    private void beginTransaction() throws SQLException {
        if(Boolean.TRUE.equals(inTransaction.get())) {
            throw new SQLException("Already in a transaction!");
        }
        connect();
        inTransaction.set(true);
        connection.setAutoCommit(false);
        transactionConnection.set(connection);
    }

    private void commitTransaction() {
        try {
            Connection connection = transactionConnection.get();
            if(connection != null) {
                connection.commit();
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error during commit!", e);
        }
    }

    private void rollbackTransaction()  {
        try {
            Connection connection = transactionConnection.get();
            if(connection != null) {
                connection.rollback();
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error during rollback!", e);
        }
    }

    private void endTransaction() {
        try {
            disconnect();
        } finally {
            transactionConnection.remove();
            inTransaction.set(false);
        }
    }

    @FunctionalInterface
    public interface TransactionCallback<T> {
        T execute() throws SQLException;
    }

    public <T> QueryResult<T> execute_query(String sql, RowMapper<T> mapper, Object... params) {
        QueryResult<T> query_result;
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set dynamic parameters
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            // multi-row fetch
            statement.setFetchSize(0);
            if(isSelectQuery(sql)) {
                query_result = execute_select(statement, mapper);
            }else {
                query_result = execute_update(statement, mapper);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if (!Boolean.TRUE.equals(inTransaction.get()) && connection != null) {
                disconnect();
            }
        }
        return query_result;
    }

    private <T> QueryResult<T> execute_select(PreparedStatement statement, RowMapper<T> mapper) {
        List<T> processed_rows;
        try {
            ResultSet rs = statement.executeQuery();
            processed_rows = mapper.process(rs);

            rs.close();
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return QueryResult.ofSelect(processed_rows);
    }

    private <T> QueryResult<T> execute_update(PreparedStatement statement, RowMapper<T> mapper) {
        long id;
        T result;
        try {
            int affectedRows = statement.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("No rows affected!");
            }

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong(1);
                // Process the result only if needed
                List<T> processed_rows = mapper.process(rs);
                result = !processed_rows.isEmpty() ? processed_rows.getFirst() : null;
            }else {
                throw new SQLException("No generated key returned!");
            }

            rs.close();
            statement.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return QueryResult.ofInsert(id, result);
    }

    private boolean isSelectQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }
}