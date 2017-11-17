package no.kij.socketscheduler.server.db;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import no.kij.socketscheduler.server.dao.SubjectDaoImpl;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectLecturerDTO;
import no.kij.socketscheduler.server.util.ConnectionManager;
import no.kij.socketscheduler.server.util.ResourceFetcher;
import org.h2.tools.Server;
import org.junit.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseInitializerTest {
    private static Server server;
    private static ConnectionManager connectionManager;
    private static SubjectDaoImpl dao;
    private static DatabaseInitializer dbInit;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "8372", "-tcpAllowOthers").start();
        connectionManager = new ConnectionManager(ResourceFetcher.getProperty("testProps"));
        dbInit = new DatabaseInitializer(connectionManager);
        dao = new SubjectDaoImpl(connectionManager.getConnectionSource());
    }

    @AfterClass
    public static void tearDownClass () {
        server.stop();
    }

    @After
    public void tearDown() throws SQLException {
        dao.executeRaw("DROP ALL OBJECTS");
    }

    @Test
    public void testInitializeTables() throws SQLException {
        dbInit.initializeTables();
        GenericRawResults<String[]> results = dao.queryRaw("SHOW TABLES");
        ArrayList<String> tables = new ArrayList<>();

        for (String[] result : results) {
            tables.add(result[0]);
        }

        assertTrue(tables.contains("LECTURERS"));
        assertTrue(tables.contains("SUBJECT_LECTURER"));
        assertTrue(tables.contains("SUBJECTS"));

    }

    @Test
    public void initializeTableContent() throws SQLException {
        dbInit.initializeTables();
        dbInit.initializeTableContent();
        long subjectRows = dao.countOf();
        long lecturerRows = DaoManager.createDao(connectionManager.getConnectionSource(),
                LecturerDTO.class).countOf();
        long slRows = DaoManager.createDao(connectionManager.getConnectionSource(),
                SubjectLecturerDTO.class).countOf();

        assertEquals(3, subjectRows);
        assertEquals(3, lecturerRows);
        assertEquals(3, slRows);
    }

}