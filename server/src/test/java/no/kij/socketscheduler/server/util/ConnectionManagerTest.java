package no.kij.socketscheduler.server.util;

import com.j256.ormlite.support.ConnectionSource;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConnectionManagerTest {
    private static Server server;
    private ConnectionManager connectionManager;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "8372", "-tcpAllowOthers").start();
    }

    @AfterClass
    public static void tearDownClass() { server.stop(); }

    @Before
    public void setUp() {
        connectionManager = new ConnectionManager(ResourceFetcher.getProperty("testProps"));
    }

    @Test
    public void testGetConnectionSourceReturnsConnectionSource() {
        ConnectionSource connectionSource = connectionManager.getConnectionSource();
        assertTrue(connectionSource.isOpen("lecturers"));
    }

    @Test
    public void testGetConnectionSourceAlwaysReturnsSameConnection() {
        ConnectionSource connectionSource = connectionManager.getConnectionSource();
        ConnectionSource connectionSource2 = connectionManager.getConnectionSource();
        assertSame(connectionSource, connectionSource2);
    }

    @Test
    public void testGetConnectionSourceReopensConnectionWhenClosed() {
        ConnectionSource connectionSource = connectionManager.getConnectionSource();
        assertTrue(connectionSource.isOpen("table"));
        connectionManager.close();
        assertFalse(connectionSource.isOpen("table"));
        connectionSource = connectionManager.getConnectionSource();
        assertTrue(connectionSource.isOpen("table"));
    }

    @Test
    public void testCloseClosesConnectionSource() {
        ConnectionSource connectionSource = connectionManager.getConnectionSource();
        assertTrue(connectionSource.isOpen("table"));
        connectionManager.close();
        assertFalse(connectionSource.isOpen("table"));
    }

    @Test
    public void testConnectionManagerWithOnlyDatabaseUrl() {
        Properties creds = ResourceFetcher.getProperty("testProps");
        creds.remove("username");
        creds.remove("password");
        connectionManager = new ConnectionManager(creds);
        ConnectionSource connectionSource = connectionManager.getConnectionSource();
        assertTrue(connectionSource.isOpen("test"));
    }

}