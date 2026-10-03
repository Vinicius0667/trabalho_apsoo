package com.agenda.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton: existe apenas uma instância desta classe (e uma conexão aberta)
 * durante toda a execução do programa.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    // Construtor privado: ninguém de fora consegue fazer "new DatabaseConnection()"
    private DatabaseConnection() {
        this.url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5433/agenda");
        this.user = System.getenv().getOrDefault("DB_USER", "root");
        this.password = System.getenv().getOrDefault("DB_PASSWORD", "root");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public synchronized void fecharConexao() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
