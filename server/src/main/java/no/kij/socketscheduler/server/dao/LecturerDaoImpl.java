package no.kij.socketscheduler.server.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectDTO;
import no.kij.socketscheduler.server.dto.SubjectLecturerDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * JDBC specific implementation for the LecturerDao interface.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
public class LecturerDaoImpl extends BaseDaoImpl<LecturerDTO, Integer> implements LecturerDao {
    private Dao<SubjectDTO, Integer> subjectDao;
    private Dao<SubjectLecturerDTO, Integer> subjectLecturerDao;
    public LecturerDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, LecturerDTO.class);
        subjectLecturerDao = DaoManager.createDao(connectionSource, SubjectLecturerDTO.class);
        subjectDao = DaoManager.createDao(connectionSource, SubjectDTO.class);
    }

    /**
     * Find a lecturer by name
     * @param name Name to search for
     * @return LecturerDTO if found, null if not
     */
    public LecturerDTO queryForLecturerName(String name) {
        LecturerDTO lecturerDTO = null;
        try {
            QueryBuilder<LecturerDTO, Integer> queryBuilder = queryBuilder();
            queryBuilder.where().eq(LecturerDTO.LECTURER_NAME_FIELD, name);
            PreparedQuery<LecturerDTO> preparedQuery = queryBuilder.prepare();
            lecturerDTO = queryForFirst(preparedQuery);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return lecturerDTO;
    }

    /**
     * Find a lecturer using a partial name
     * @param partialName The partial name to be found
     * @return LecturerDTO containing the lecturer, returns null if not found
     */
    public LecturerDTO queryForPartialName(String partialName) {
        LecturerDTO lecturerDTO = null;
        try {
            QueryBuilder<LecturerDTO, Integer> queryBuilder = queryBuilder();
            queryBuilder.where().like(LecturerDTO.LECTURER_NAME_FIELD, "%" + partialName + "%");
            PreparedQuery<LecturerDTO> preparedQuery = queryBuilder.prepare();
            lecturerDTO = queryForFirst(preparedQuery);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return lecturerDTO;
    }

    /**
     * Searches both for an exact match and a partial match, if exact is not found.
     * @param name Name of the lecturer to find
     * @return LecturerDTO if found, null if not
     */
    public LecturerDTO queryForExactOrPartialName(String name) {
        LecturerDTO lecturerDTO = queryForLecturerName(name);
        if (lecturerDTO == null) {
            lecturerDTO = queryForPartialName(name);
        }
        return lecturerDTO;
    }

    /**
     * Returns a list of all subjects for a given lecturer.
     * @param data Lecturer to find subjects for
     * @return List containing SubjectDTO
     * @throws SQLException If something goes wrong while querying
     */
    public List<SubjectDTO> findSubjectsForLecturer(LecturerDTO data) throws SQLException {
        return lookupSubjectsForLecturer(data);
    }

    /**
     * The private implementation for finding the subjects for the lecturer.
     * @param data Lecturer to find subjects for
     * @return List of subject for said lecturer
     * @throws SQLException If something goes wrong while querying
     */
    private List<SubjectDTO> lookupSubjectsForLecturer(LecturerDTO data) throws SQLException {
        PreparedQuery<SubjectDTO> subjectsForLecturerQuery = makeSubjectForLecturerQuery();
        subjectsForLecturerQuery.setArgumentHolderValue(0, data);
        return subjectDao.query(subjectsForLecturerQuery);
    }


    /**
     * Creates the query to fetch lecturers for subject.
     * @return PreparedQuery containing the query
     */
    private PreparedQuery<SubjectDTO> makeSubjectForLecturerQuery() throws SQLException {
        QueryBuilder<SubjectLecturerDTO, Integer> subLecQb = subjectLecturerDao.queryBuilder();

        subLecQb.selectColumns(SubjectLecturerDTO.SUBJECT_ID_FIELD);
        SelectArg subjectSelectArg = new SelectArg();

        subLecQb.where().eq(SubjectLecturerDTO.LECTURER_ID_FIELD, subjectSelectArg);

        QueryBuilder<SubjectDTO, Integer> subjectQb = subjectDao.queryBuilder();
        subjectQb.where().in(SubjectLecturerDTO.ID_FIELD, subLecQb);
        return subjectQb.prepare();
    }
}
