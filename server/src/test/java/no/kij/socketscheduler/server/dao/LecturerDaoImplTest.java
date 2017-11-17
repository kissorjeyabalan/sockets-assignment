package no.kij.socketscheduler.server.dao;

import no.kij.socketscheduler.server.db.DatabaseInitializer;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectDTO;
import no.kij.socketscheduler.server.util.ConnectionManager;
import no.kij.socketscheduler.server.util.ResourceFetcher;
import org.h2.tools.Server;
import org.junit.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class LecturerDaoImplTest {
    private static Server server;
    private static ConnectionManager connectionManager;
    private static DatabaseInitializer dbInit;
    private LecturerDaoImpl lecturerDaoImpl;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "8372", "-tcpAllowOthers").start();
        connectionManager = new ConnectionManager(ResourceFetcher.getProperty("testProps"));
        dbInit = new DatabaseInitializer(connectionManager);
    }

    @AfterClass
    public static void tearDownClass() {
        server.stop();
        connectionManager.close();
    }

    @Before
    public void setUp() throws SQLException {
        lecturerDaoImpl = new LecturerDaoImpl(connectionManager.getConnectionSource());
        dbInit.initializeTables();
        dbInit.initializeTableContent();
    }

    @After
    public void tearDown() throws SQLException {
        lecturerDaoImpl.executeRaw("DROP ALL OBJECTS");
    }

    @Test
    public void testQueryForLecturerNameReturnsValidLecturer() throws SQLException {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForLecturerName("Vilde Birkenes");
        LecturerDTO lecturer2 = lecturerDaoImpl.queryForLecturerName("Lilly Evensen");

        assertEquals(Integer.valueOf(2), lecturer1.getId());
        assertEquals("Vilde Birkenes", lecturer1.getName());

        assertEquals(Integer.valueOf(3), lecturer2.getId());
        assertEquals("Lilly Evensen", lecturer2.getName());
    }

    @Test
    public void testQueryForLecturerNameReturnsNullIfNotFound() {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForLecturerName("Does Not Exist");
        LecturerDTO lecturer2 = lecturerDaoImpl.queryForLecturerName("Also Does Not Exist");

        assertNull(lecturer1);
        assertNull(lecturer2);
    }

    @Test
    public void testQueryForLecturerReturnsNullUponSQLFailure() {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForLecturerName(null);
        assertNull(lecturer1);
    }

    @Test
    public void testQueryForPartialNameReturnsValidLecturer() {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForPartialName("Lilly");
        LecturerDTO lecturer2 = lecturerDaoImpl.queryForPartialName("Birke");
        LecturerDTO lecturer3 = lecturerDaoImpl.queryForPartialName("Alex");

        assertEquals(Integer.valueOf(3), lecturer1.getId());
        assertEquals("Lilly Evensen", lecturer1.getName());

        assertEquals(Integer.valueOf(2), lecturer2.getId());
        assertEquals("Vilde Birkenes", lecturer2.getName());

        assertEquals(Integer.valueOf(1), lecturer3.getId());
        assertEquals("Alexander Melby", lecturer3.getName());
    }

    @Test
    public void testQueryForPartialNameReturnsNullIfNotFound() {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForPartialName("Bjerke");
        LecturerDTO lecturer2 = lecturerDaoImpl.queryForPartialName("Aleks");

        assertNull(lecturer1);
        assertNull(lecturer2);
    }

    @Test
    public void testQueryForExactOrPartialNameReturnsValidLecturer() {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForExactOrPartialName("Birke");
        LecturerDTO lecturer2 = lecturerDaoImpl.queryForExactOrPartialName("Vilde Birkenes");

        assertEquals(Integer.valueOf(2), lecturer1.getId());
        assertEquals("Vilde Birkenes", lecturer1.getName());
        assertEquals(Integer.valueOf(2), lecturer2.getId());
        assertEquals("Vilde Birkenes", lecturer2.getName());
    }

    @Test
    public void testQueryForExactOrPartialNameReturnsNullIfNotFound() {
        LecturerDTO lecturer1 = lecturerDaoImpl.queryForExactOrPartialName("BAsdfadsf");
        assertNull(lecturer1);
    }

    @Test
    public void testFindSubjectsForLecturer() throws SQLException {
        LecturerDTO lecturer = lecturerDaoImpl.queryForId(1);
        List<SubjectDTO> subjectList = lecturerDaoImpl.findSubjectsForLecturer(lecturer);

        assertEquals(1, subjectList.size());
        assertEquals(Integer.valueOf(1), subjectList.get(0).getId());
        assertEquals("Avansert Javaprogrammering", subjectList.get(0).getName());
    }

}