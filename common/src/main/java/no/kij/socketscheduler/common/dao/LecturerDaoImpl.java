package no.kij.socketscheduler.common.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import no.kij.socketscheduler.common.dto.LecturerDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * JDBC specific implementation for the LecturerDao interface.
 */
public class LecturerDaoImpl extends BaseDaoImpl<LecturerDTO, Integer> implements LecturerDao {

    public LecturerDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, LecturerDTO.class);
    }

    /**
     * Find a lecturer by name
     * @param name Name to search for
     * @return LecturerDTO if found, null if not
     * @throws SQLException If something goes wrong while querying for the given name
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
        return null;
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
}
