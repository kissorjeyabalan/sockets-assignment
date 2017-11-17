package no.kij.socketscheduler.server.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import no.kij.socketscheduler.server.dao.LecturerDaoImpl;

/**
 * This class is the data transfer object for the lecturers.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
@DatabaseTable(tableName = "lecturers", daoClass = LecturerDaoImpl.class)
public class LecturerDTO {
    public static final String ID_FIELD = "id";
    public static final String LECTURER_NAME_FIELD = "name";

    @DatabaseField(generatedId = true, columnName = ID_FIELD)
    private Integer id;
    @DatabaseField(canBeNull = false, columnName = LECTURER_NAME_FIELD)
    private String name;

    public LecturerDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
