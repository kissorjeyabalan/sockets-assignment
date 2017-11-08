package no.kij.socketscheduler.server.db;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Properties;

/**
 * The purpose of this class is to manage the database connections.
 * It helps pool the connections, so they can be reused.
 */
public class ConnectionManager {
    private ConnectionSource connectionSource;
    private Properties credentials;

    /**
     * Creates an instance of the ConnectionManager.
     * @param creds Properties file containing the database details
     */
    public ConnectionManager(Properties creds) {
        credentials = creds;
        connectionSource = null;
        openPooledConnection();
    }

    /**
     * Gets a connection from the existing connection pool base-
     * @return ConnectionSource containing an active connection
     */
    public ConnectionSource getConnectionSource() {
        if (connectionSource != null) {
            return connectionSource;
        } else {
            openPooledConnection();
            return getConnectionSource();
        }
    }

    /**
     * Used to reopen a closed ConnectionManager.
     */
    public void open() {
        openPooledConnection();
    }

    /**
     * Used to close the ConnectionManager and kill any active connections.
     */
    public void close() {
        connectionSource.closeQuietly();
        connectionSource = null;
    }

    /**
     * Opens the pooled connection using the provided properties file.
     */
    private void openPooledConnection() {
        if (connectionSource == null) {
            try {
                connectionSource = new JdbcPooledConnectionSource(
                        credentials.getProperty("database_url"),
                        credentials.getProperty("username"),
                        credentials.getProperty("password")
                );
            } catch (SQLException e) {
                System.err.println("Couldn't open Pooled Connection");
                System.err.println(e.getMessage());
            }
        }
    }

}