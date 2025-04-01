package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    static Connection connection;
    DBManager() {};

    private void connect() {
        String jdbcUrl = System.getenv("JDBC_URL");
        String username = System.getenv("JDBC_USERNAME");
        String password = System.getenv("JDBC_PASSWORD");
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

    public void execute_statement(String sql) {
        connect();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        disconnect();
    }
}
