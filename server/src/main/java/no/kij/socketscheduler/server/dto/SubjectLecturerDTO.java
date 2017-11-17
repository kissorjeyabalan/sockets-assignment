package no.kij.socketscheduler.server.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is the Data Transfer Object for the subject_lecturer table.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
@DatabaseTable(tableName = "subject_lecturer")
public class SubjectLecturerDTO {
    public static final String ID_FIELD = "id";
    public static final String SUBJECT_ID_FIELD = "subject_id";
    public static final String LECTURER_ID_FIELD = "lecturer_id";

    @DatabaseField(generatedId = true, columnName = ID_FIELD)
    private Integer id;
    @DatabaseField(foreign = true, columnName = SUBJECT_ID_FIELD)
    private SubjectDTO subjectDTO;
    @DatabaseField(foreign = true, columnName = LECTURER_ID_FIELD)
    private LecturerDTO lecturerDTO;

    public SubjectLecturerDTO() {}

    public void setSubjectDTO(SubjectDTO subjectDTO) {
        this.subjectDTO = subjectDTO;
    }

    public void setLecturerDTO(LecturerDTO lecturerDTO) {
        this.lecturerDTO = lecturerDTO;
    }
}
