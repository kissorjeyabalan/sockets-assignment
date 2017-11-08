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
     * @return LecturerDTO if found
     * @throws SQLException If something goes wrong while querying for the given name
     */
    @Override
    public LecturerDTO queryForLecturerName(String name) throws SQLException {
        QueryBuilder<LecturerDTO, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq(LecturerDTO.LECTURER_NAME_FIELD, name);
        PreparedQuery<LecturerDTO> preparedQuery = queryBuilder.prepare();
        return queryForFirst(preparedQuery);
    }

}
