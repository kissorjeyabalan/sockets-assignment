package no.kij.socketscheduler.server.dao;

import com.j256.ormlite.dao.Dao;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface for LecturerDao, declaring custom methods.
 */
public interface LecturerDao extends Dao<LecturerDTO, Integer> {
    LecturerDTO queryForLecturerName(String name);
    LecturerDTO queryForPartialName(String partialName);
    LecturerDTO queryForExactOrPartialName(String name);
    List<SubjectDTO> findSubjectsForLecturer(LecturerDTO lecturerDTO) throws SQLException;
}
