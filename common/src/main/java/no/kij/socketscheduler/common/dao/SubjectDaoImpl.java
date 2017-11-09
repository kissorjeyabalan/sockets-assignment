package no.kij.socketscheduler.common.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import no.kij.socketscheduler.common.dto.LecturerDTO;
import no.kij.socketscheduler.common.dto.SubjectDTO;
import no.kij.socketscheduler.common.dto.SubjectLecturerDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * JDBC specific implementation for the SubjectDao interface.
 */
public class SubjectDaoImpl extends BaseDaoImpl<SubjectDTO, Integer> implements SubjectDao {
    private Dao<SubjectLecturerDTO, Integer> subjectLecturerDao;
    private Dao<LecturerDTO, Integer> lecturerDao;

    public SubjectDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, SubjectDTO.class);
        subjectLecturerDao = DaoManager.createDao(connectionSource, SubjectLecturerDTO.class);
        lecturerDao = DaoManager.createDao(connectionSource, LecturerDTO.class);
    }

    @Override
    public SubjectDTO queryForId(Integer integer) throws SQLException {
        SubjectDTO subject = super.queryForId(integer);
        subject.setLecturers(findLecturersForSubject(subject));
        return subject;
    }

    @Override
    public List<SubjectDTO> queryForAll() throws SQLException {
        List<SubjectDTO> subjectDTOs = super.queryForAll();
        for (SubjectDTO subjectDTO : subjectDTOs) {
            subjectDTO.setLecturers(findLecturersForSubject(subjectDTO));
        }
        return subjectDTOs;
    }

    /**
     * Persists the given subject to the database if they already do not exist.
     * @param data The subjectDTO to persist
     * @return SubjectDTO with updates ID fields
     * @throws SQLException If something goes wrong
     */
    @Override
    public SubjectDTO createIfNotExists(SubjectDTO data) throws SQLException {
        super.createIfNotExists(data);
        for (LecturerDTO lecturerDTO : data.getLecturers()) {
            lecturerDao.createIfNotExists(lecturerDTO);
            SubjectLecturerDTO sld = new SubjectLecturerDTO();
            sld.setLecturerDTO(lecturerDTO);
            sld.setSubjectDTO(data);
            subjectLecturerDao.createIfNotExists(sld);
        }
        return data;
    }

    /**
     * Update the given subject. It is created if it already does not exist.
     * @param data SubjectDTO to update
     * @return SubjectDTO with updated ID fields
     * @throws SQLException IF something goes wrong
     */
    public SubjectDTO updateSubject(SubjectDTO data) throws SQLException {
        for (LecturerDTO lecturerDTO : data.getLecturers()) {
            lecturerDao.createOrUpdate(lecturerDTO);
            SubjectLecturerDTO sld = new SubjectLecturerDTO();
            sld.setLecturerDTO(lecturerDTO);
            sld.setSubjectDTO(data);
            subjectLecturerDao.createOrUpdate(sld);
        }
        return data;

    }

    /**
     * Returns a list of all lecturers for the given subject
     * @param data Subject to find lecturers for
     * @return List containing LecturerDTO
     * @throws SQLException If something goes wrong while querying
     */
    public List<LecturerDTO> findLecturersForSubject(SubjectDTO data) throws SQLException {
        return lookupLecturersForSubject(data);
    }

    public List<SubjectDTO> findSubjectsForLecturer(LecturerDTO data) throws SQLException {
        return lookupSubjectsForLecturer(data);
    }


    /**
     * The private implementation for finding the lecturers for the subject.
     * @param data Subject to find lecturers for
     * @return List of lecturers for said subject
     * @throws SQLException If something goes wrong while querying
     */
    private List<LecturerDTO> lookupLecturersForSubject(SubjectDTO data) throws SQLException {
        PreparedQuery<LecturerDTO> lecturersForSubjectQuery = makeLecturerForSubjectQuery();
        lecturersForSubjectQuery.setArgumentHolderValue(0, data);
        return lecturerDao.query(lecturersForSubjectQuery);
    }

    private List<SubjectDTO> lookupSubjectsForLecturer(LecturerDTO data) throws SQLException {
        PreparedQuery<SubjectDTO> subjectsForLecturerQuery = makeSubjectForLecturerQuery();
        subjectsForLecturerQuery.setArgumentHolderValue(0, data);
        return query(subjectsForLecturerQuery);
    }



    private PreparedQuery<SubjectDTO> makeSubjectForLecturerQuery() throws SQLException {
        QueryBuilder<SubjectLecturerDTO, Integer> subLecQb = subjectLecturerDao.queryBuilder();

        subLecQb.selectColumns(SubjectLecturerDTO.SUBJECT_ID_FIELD);
        SelectArg subjectSelectArg = new SelectArg();

        subLecQb.where().eq(SubjectLecturerDTO.LECTURER_ID_FIELD, subjectSelectArg);

        QueryBuilder<SubjectDTO, Integer> subjectQb = queryBuilder();
        subjectQb.where().in(SubjectLecturerDTO.ID_FIELD, subLecQb);
        return subjectQb.prepare();
    }

    /**
     * Creates the query used to find the lecturers for given subject.
     * @return PreparedQuery for finding lecturers
     * @throws SQLException If query could not be prepared
     */
    private PreparedQuery<LecturerDTO> makeLecturerForSubjectQuery() throws SQLException {
        QueryBuilder<SubjectLecturerDTO, Integer> subjectLecturerQb = subjectLecturerDao.queryBuilder();

        subjectLecturerQb.selectColumns(SubjectLecturerDTO.LECTURER_ID_FIELD);
        SelectArg lecturerSelectArg = new SelectArg();
        subjectLecturerQb.where().eq(SubjectLecturerDTO.SUBJECT_ID_FIELD, lecturerSelectArg);

        QueryBuilder<LecturerDTO, Integer> lecturerQb = lecturerDao.queryBuilder();

        lecturerQb.where().in(SubjectLecturerDTO.ID_FIELD, subjectLecturerQb);
        return lecturerQb.prepare();
    }
}
