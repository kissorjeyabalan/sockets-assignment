package no.kij.socketscheduler.server.db;

import com.google.gson.*;
import com.j256.ormlite.table.TableUtils;
import no.kij.socketscheduler.server.dao.LecturerDao;
import no.kij.socketscheduler.server.dao.SubjectDao;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectDTO;
import no.kij.socketscheduler.server.dto.SubjectLecturerDTO;
import no.kij.socketscheduler.server.util.ConnectionManager;
import no.kij.socketscheduler.server.util.DaoDelegator;
import no.kij.socketscheduler.server.util.ResourceFetcher;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The purpose of this class is to setup the base structure for the database using the DTO class structure,
 * as well as populating the fields for the database with some default content.
 */
public class DatabaseInitializer {
    private ConnectionManager connectionManager;

    /**
     * Creates the database initializer to initialize the database.
     * @param connectionManager The ConnectionManager to pool database connections from
     */
    public DatabaseInitializer(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Initializes the database structure.
     */
    public void initializeTables() {
        try {
            // dropping tables if they already exist, since we are not checking for first time run
            // and we dont want multiples of same values in db
            TableUtils.dropTable(connectionManager.getConnectionSource(), SubjectDTO.class, true);
            TableUtils.dropTable(connectionManager.getConnectionSource(), LecturerDTO.class, true);
            TableUtils.dropTable(connectionManager.getConnectionSource(), SubjectLecturerDTO.class, true);

            // create the tables
            TableUtils.createTableIfNotExists(connectionManager.getConnectionSource(), SubjectDTO.class);
            TableUtils.createTableIfNotExists(connectionManager.getConnectionSource(), LecturerDTO.class);
            TableUtils.createTableIfNotExists(connectionManager.getConnectionSource(), SubjectLecturerDTO.class);
        } catch (SQLException e) {
            System.err.println("Could not create the tables for the database.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initializes the database tables with default content from "subjects.json" and "lecturers.json" in resources folder.
     */
    public void initializeTableContent() {
        try {
            // Create instance of gson. It's a library we're using to map json to java objects
            DaoDelegator dao = new DaoDelegator(connectionManager);
            Gson gson = new Gson();

            System.out.println("Inserting lecturers...");
            String json = ResourceFetcher.getFile("lecturers.json");
            LecturerDTO[] lecturers = gson.fromJson(json, LecturerDTO[].class);
            LecturerDao lecturerDao = dao.getLecturerDao();

            for (LecturerDTO lecturer :lecturers) {
                lecturerDao.createIfNotExists(lecturer);
            }

            System.out.println("Inserting subjects...");
            json = ResourceFetcher.getFile("subjects.json");
            SubjectDTO[] subjects = gson.fromJson(json, SubjectDTO[].class);
            SubjectDao subjectDao = dao.getSubjectDao();

            for (SubjectDTO subject : subjects) {
                // we are resetting the lecturers first, as it is got filled with garbage by gson
                // when it was deserialized.
                subject.setLecturers(new ArrayList<>());
                subjectDao.createIfNotExists(subject);
            }


            System.out.println("Binding lecturers to subjects...");
            JsonParser parser = new JsonParser();
            JsonArray subjectJsonArr = parser.parse(json).getAsJsonArray();

            // here we are iterating through all the subject objects in the json
            // then we isolate the lecturers
            for (int i = 0; i < subjects.length; i++) {
                JsonObject subjectObj = subjectJsonArr.get(i).getAsJsonObject();
                JsonArray lecturerArr = subjectObj.getAsJsonArray("lecturers");

                // we iterate through all the lecturers and make sure they are not null,
                // before we query for the lecturer in our database to find them.
                // we then add them to the list of subjects.
                for (JsonElement lecturerElement : lecturerArr) {
                    JsonObject lecturerObj = lecturerElement.getAsJsonObject();
                    String lecturerName = lecturerObj.get("name").getAsString();
                    if (lecturerName != null){
                        LecturerDTO lecturerDTO = lecturerDao.queryForLecturerName(lecturerName);
                        if (lecturerDTO != null) {
                            subjects[i].getLecturers().add(lecturerDTO);
                        }
                    }
                }
            }

            // we call an update, which updates the subjects with the lecturer details,
            // as well as creates any lecturers that were missing (if they were missing).
            for (SubjectDTO subject : subjects) {
                subjectDao.updateSubject(subject);
            }

        } catch (SQLException e) {
            System.out.println("Could not initialize the tables with content.");
            System.err.println(e.getMessage());
        }
    }
}
