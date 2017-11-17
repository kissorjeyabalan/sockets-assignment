package no.kij.socketscheduler.server.util;

import no.kij.socketscheduler.server.dao.LecturerDao;
import no.kij.socketscheduler.server.dao.LecturerDaoImpl;
import no.kij.socketscheduler.server.dao.SubjectDao;
import no.kij.socketscheduler.server.dao.SubjectDaoImpl;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DaoDelegatorTest {
    private static Server server;
    private static ConnectionManager connectionManager;
    private DaoDelegator daoDelegator;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "8372", "-tcpAllowOthers").start();
        connectionManager = new ConnectionManager(ResourceFetcher.getProperty("testProps"));
    }

    @AfterClass
    public static void tearDownClass() { server.stop(); }

    @Before
    public void setUp() {
        daoDelegator = new DaoDelegator(connectionManager);
    }

    @Test
    public void testGetSubjectDaoReturnsNewDao() {
        SubjectDao subjectDao = daoDelegator.getSubjectDao();
        assertTrue(subjectDao.getClass() == SubjectDaoImpl.class);
    }

    @Test
    public void testGetLecturerDaoReturnsNewDao() {
        LecturerDao lecturerDao = daoDelegator.getLecturerDao();
        assertTrue(lecturerDao.getClass() == LecturerDaoImpl.class);
    }

    @Test
    public void testGetSubjectDaoReturnsSameDaoAlways() {
        SubjectDao subjectDao = daoDelegator.getSubjectDao();
        assertTrue(subjectDao.getClass() == SubjectDaoImpl.class);

        SubjectDao duplicateSubjectDao = daoDelegator.getSubjectDao();
        assertSame(subjectDao, duplicateSubjectDao);
    }

    @Test
    public void testGetLecturerDaoReturnsSameDaoAlways() {
        LecturerDao lecturerDao = daoDelegator.getLecturerDao();
        assertTrue(lecturerDao.getClass() == LecturerDaoImpl.class);

        LecturerDao duplicateLecturerDao = daoDelegator.getLecturerDao();
        assertSame(lecturerDao, duplicateLecturerDao);
    }




}