package no.kij.socketscheduler.server.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import no.kij.socketscheduler.server.dao.SubjectDao;
import no.kij.socketscheduler.server.dao.SubjectDaoImpl;
import no.kij.socketscheduler.server.dao.LecturerDao;
import no.kij.socketscheduler.server.dao.LecturerDaoImpl;
import no.kij.socketscheduler.server.dto.SubjectLecturerDTO;

import java.sql.SQLException;

/**
 * A delegator which is used to retrieve Dao's.
 */
public class DaoDelegator {
    private ConnectionManager connectionManager;
    private SubjectDao subjectDao;
    private LecturerDao lecturerDao;

    public DaoDelegator(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Fetch a dao to manipulate the subjects table.
     * @return SubjectDao
     */
    public SubjectDao getSubjectDao() {
        if (subjectDao != null) {
            return subjectDao;
        }
        try {
            subjectDao = new SubjectDaoImpl(connectionManager.getConnectionSource());
            return subjectDao;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Fetch a dao used to manipulate the lecturers table.
     * @return LecturerDao
     */
    public LecturerDao getLecturerDao() {
        if (lecturerDao != null) {
            return lecturerDao;
        }
        try {
            lecturerDao = new LecturerDaoImpl(connectionManager.getConnectionSource());
            return lecturerDao;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
