package no.kij.socketscheduler.common.dao;

import com.j256.ormlite.dao.Dao;
import no.kij.socketscheduler.common.dto.LecturerDTO;
import no.kij.socketscheduler.common.dto.SubjectDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface for LecturerDao, declaring custom methods.
 */
public interface LecturerDao extends Dao<LecturerDTO, Integer> {
    LecturerDTO queryForLecturerName(String name);
    LecturerDTO queryForPartialName(String partialName);
    LecturerDTO queryForExactOrPartialName(String name);
}
