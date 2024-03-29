package no.kij.socketscheduler.server.dao;

import com.j256.ormlite.dao.Dao;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface for SubjectDao adding more methods.
 */
public interface SubjectDao extends Dao<SubjectDTO, Integer> {
    List<LecturerDTO> findLecturersForSubject(SubjectDTO subjectDTO) throws SQLException;
    SubjectDTO updateSubject(SubjectDTO subjectDTO) throws SQLException;
    SubjectDTO findSubjectByCode(String subjectCode);
    SubjectDTO findSubjectByName(String subjectName);
    SubjectDTO findSubjectByCodeOrName(String subject);
}
