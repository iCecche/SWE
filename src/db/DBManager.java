package db;

import rowmapper.RowMapper;

import java.sql.*;
import java.util.List;

public class DBManager {

    private static DBManager instance = null;
    private Connection connection;

    // TODO: use singleton for single DB Manager and/or single connection
    private DBManager() {};

    public static DBManager getInstance() throws SQLException {
        if(instance == null || instance.getConnection().isClosed()) {
            instance = new DBManager();
            instance.connect();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void connect() {
        final String jdbcUrl = System.getenv("JDBC_URL");
        final String username = System.getenv("JDBC_USERNAME");
        final String password = System.getenv("JDBC_PASSWORD");
        try {
            // Register the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            // Connect to the database
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

    public <T> List<T> execute_statement(String sql, RowMapper<T> mapper, Object... params) {
        ResultSet rs;
        List<T> processed_rows;
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set dynamic parameters
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            // multi-row fetch
            statement.setFetchSize(0);

            if (sql.trim().toLowerCase().startsWith("select")) {
                rs = statement.executeQuery();  // Execute SELECT
            } else {
                statement.executeUpdate();  // Execute INSERT, UPDATE, DELETE
                rs = statement.getGeneratedKeys();
            }

            // map ResultSet to correct type java object
            processed_rows = mapper.process(rs);

            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        disconnect();
        return processed_rows;
    }
}
