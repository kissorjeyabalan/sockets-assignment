package no.kij.socketscheduler.server.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import no.kij.socketscheduler.server.dao.SubjectDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Data Transfer Object for the subjects.
 */
@DatabaseTable(tableName = "subjects", daoClass = SubjectDaoImpl.class)
public class SubjectDTO {
    public static final String SHORT_CODE_FIELD = "code";
    public static final String NAME_FIELD = "name";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false, columnName = SHORT_CODE_FIELD)
    private String shortName;
    @DatabaseField(canBeNull = false, columnName = NAME_FIELD)
    private String name;
    @DatabaseField(defaultValue = "0")
    private Integer enrolled;
    private List<LecturerDTO> lecturers = new ArrayList<>();

    public SubjectDTO() {}

    public Integer getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Integer enrolled) {
        this.enrolled = enrolled;
    }

    public List<LecturerDTO> getLecturers() {
        return lecturers;
    }

    public void setLecturers(List<LecturerDTO> lecturers) {
        this.lecturers = lecturers;
    }
}
