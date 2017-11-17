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

public class SubjectDaoImplTest {
    private static Server server;
    private static ConnectionManager connectionManager;
    private static DatabaseInitializer dbInit;
    private SubjectDaoImpl subjectDaoImpl;

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
    public void setUp() throws Exception {
        subjectDaoImpl = new SubjectDaoImpl(connectionManager.getConnectionSource());
        dbInit.initializeTables();
        dbInit.initializeTableContent();
    }

    @After
    public void tearDown() throws Exception {
        subjectDaoImpl.executeRaw("DROP ALL OBJECTS");
    }

    @Test
    public void testFindSubjectByCodeOrNameReturnsValidSubjectOnCode() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByCodeOrName("PGR200");

        assertEquals(Integer.valueOf(1), subject.getId());
        assertEquals("PGR200", subject.getShortName());
        assertEquals("Avansert Javaprogrammering", subject.getName());
        assertEquals(Integer.valueOf(65), subject.getEnrolled());
        assertEquals(2, subject.getLecturers().size());
        assertEquals(Integer.valueOf(1), subject.getLecturers().get(0).getId());
        assertEquals(Integer.valueOf(2), subject.getLecturers().get(1).getId());
    }

    @Test
    public void testFindSubjectByCodeOrNameReturnsValidSubjectOnName() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByCodeOrName("Java");

        assertEquals(Integer.valueOf(1), subject.getId());
        assertEquals("PGR200", subject.getShortName());
        assertEquals("Avansert Javaprogrammering", subject.getName());
        assertEquals(Integer.valueOf(65), subject.getEnrolled());
        assertEquals(2, subject.getLecturers().size());
        assertEquals(Integer.valueOf(1), subject.getLecturers().get(0).getId());
        assertEquals(Integer.valueOf(2), subject.getLecturers().get(1).getId());
    }

    @Test
    public void testFindSubjectByCodeOrNameReturnsNullIfNotFound() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByCodeOrName("asdfasdf");
        assertNull(subject);
    }

    @Test
    public void testFindSubjectByNameReturnsValidSubjectOnPartialName() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByName("Java");

        assertEquals(Integer.valueOf(1), subject.getId());
        assertEquals("PGR200", subject.getShortName());
        assertEquals("Avansert Javaprogrammering", subject.getName());
        assertEquals(Integer.valueOf(65), subject.getEnrolled());
        assertEquals(2, subject.getLecturers().size());
        assertEquals(Integer.valueOf(1), subject.getLecturers().get(0).getId());
        assertEquals(Integer.valueOf(2), subject.getLecturers().get(1).getId());
    }

    @Test
    public void testFindSubjectByNameReturnsValidSubjectOnFullName() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByName("Algoritmer og datastrukturer");

        assertEquals(Integer.valueOf(2), subject.getId());
        assertEquals("PG4200", subject.getShortName());
        assertEquals("Algoritmer og datastrukturer", subject.getName());
        assertEquals(Integer.valueOf(79), subject.getEnrolled());
        assertEquals(0, subject.getLecturers().size());
    }

    @Test
    public void testFindSubjectByNameReturnsNullIfNotFound() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByName("asdfasdf");
        assertNull(subject);
    }

    @Test
    public void testFindSubjectByCodeReturnsValidSubject() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByCode("PGR200");

        assertEquals(Integer.valueOf(1), subject.getId());
        assertEquals("PGR200", subject.getShortName());
        assertEquals("Avansert Javaprogrammering", subject.getName());
        assertEquals(Integer.valueOf(65), subject.getEnrolled());
        assertEquals(2, subject.getLecturers().size());
    }

    @Test
    public void testFindSubjectByCodeReturnsNullIfNotFound() {
        SubjectDTO subject = subjectDaoImpl.findSubjectByCode("ASGSDG");
        assertNull(subject);
    }

    @Test
    public void testQueryForIdReturnsValidSubject() throws SQLException {
        SubjectDTO subject = subjectDaoImpl.queryForId(3);

        assertEquals(Integer.valueOf(3), subject.getId());
        assertEquals("PG3300", subject.getShortName());
        assertEquals("Software Design", subject.getName());
        assertEquals(Integer.valueOf(97), subject.getEnrolled());
        assertEquals(1, subject.getLecturers().size());
        assertEquals(Integer.valueOf(3), subject.getLecturers().get(0).getId());
    }

    @Test
    public void testQueryForIdReturnsNullIfNotFound() throws SQLException {
        SubjectDTO subject = subjectDaoImpl.queryForId(5);
        assertNull(subject);
    }

    @Test
    public void testQueryForAll() throws SQLException {
        List<SubjectDTO> subjectList = subjectDaoImpl.queryForAll();

        assertEquals(3, subjectList.size());
        assertEquals(Integer.valueOf(1), subjectList.get(0).getId());
        assertEquals(Integer.valueOf(2), subjectList.get(1).getId());
        assertEquals(Integer.valueOf(3), subjectList.get(2).getId());
    }

    @Test
    public void testCreateIfNotExistsWithNewLecturer() throws SQLException {
        LecturerDTO lecturer = new LecturerDTO();
        lecturer.setName("Ada Rosseland");

        SubjectDTO subject = new SubjectDTO();
        subject.setName("Smidig Prosjekt");
        subject.setShortName("PRO200");
        subject.setEnrolled(6);
        subject.getLecturers().add(lecturer);

        subjectDaoImpl.createIfNotExists(subject);
        SubjectDTO dbSubject = subjectDaoImpl.findSubjectByCode("PRO200");

        assertEquals(Integer.valueOf(4), dbSubject.getId());
        assertEquals("Smidig Prosjekt", dbSubject.getName());
        assertEquals("PRO200", dbSubject.getShortName());
        assertEquals(Integer.valueOf(6), dbSubject.getEnrolled());
        assertEquals(1, dbSubject.getLecturers().size());

        assertEquals(Integer.valueOf(4), dbSubject.getLecturers().get(0).getId());
        assertEquals("Ada Rosseland", dbSubject.getLecturers().get(0).getName());
    }

    @Test
    public void testCreateIfNotExistsWithExistingLecturer() throws SQLException {
        LecturerDTO lecturer = new LecturerDTO();
        lecturer.setId(1);

        SubjectDTO subject = new SubjectDTO();
        subject.setName("Smidig Prosjekt");
        subject.setShortName("PRO200");
        subject.setEnrolled(6);
        subject.getLecturers().add(lecturer);

        subjectDaoImpl.createIfNotExists(subject);
        SubjectDTO dbSubject = subjectDaoImpl.findSubjectByCode("PRO200");

        assertEquals(Integer.valueOf(4), dbSubject.getId());
        assertEquals("Smidig Prosjekt", dbSubject.getName());
        assertEquals("PRO200", dbSubject.getShortName());
        assertEquals(Integer.valueOf(6), dbSubject.getEnrolled());
        assertEquals(1, dbSubject.getLecturers().size());

        assertEquals(Integer.valueOf(1), dbSubject.getLecturers().get(0).getId());
        assertEquals("Alexander Melby", dbSubject.getLecturers().get(0).getName());
    }

    @Test
    public void testCreateIfNotExistsWithExistingSubject() throws SQLException {
        SubjectDTO subject = subjectDaoImpl.queryForId(3);
        subjectDaoImpl.createIfNotExists(subject);
        SubjectDTO nextSubject = subjectDaoImpl.queryForId(4);

        assertNull(nextSubject);

    }


    @Test
    public void updateSubject() throws Exception {
        SubjectDTO subject = subjectDaoImpl.queryForId(1);
        assertEquals("PGR200", subject.getShortName());

        subject.setShortName("PGR240");
        subjectDaoImpl.updateSubject(subject);

        SubjectDTO updatedSubjectFromDb = subjectDaoImpl.queryForId(1);
        assertEquals("PGR240", updatedSubjectFromDb.getShortName());
    }

    @Test
    public void findLecturersForSubject() throws Exception {
        SubjectDTO subject = subjectDaoImpl.queryForId(1);
        List<LecturerDTO> lecturerList = subjectDaoImpl.findLecturersForSubject(subject);

        assertEquals(2, lecturerList.size());
        assertEquals("Alexander Melby", lecturerList.get(0).getName());
        assertEquals("Vilde Birkenes", lecturerList.get(1).getName());
    }
}