package no.kij.socketscheduler.common.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import no.kij.socketscheduler.common.dao.SubjectDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Data Transfer Object for the subjects.
 */
@DatabaseTable(tableName = "subjects", daoClass = SubjectDaoImpl.class)
public class SubjectDTO {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String shortName;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(defaultValue = "0")
    private Integer enrolled;
    private List<LecturerDTO> lecturers = new ArrayList<>();

    public SubjectDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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