package no.kij.socketscheduler.server;

import no.kij.socketscheduler.server.cmd.CommandDetails;
import no.kij.socketscheduler.server.cmd.CommandParser;
import no.kij.socketscheduler.server.cmd.CommandType;
import no.kij.socketscheduler.server.dto.LecturerDTO;
import no.kij.socketscheduler.server.dto.SubjectDTO;
import no.kij.socketscheduler.server.util.ConnectionManager;
import no.kij.socketscheduler.server.util.DaoDelegator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class used to thread incoming client connections to a new thread,
 * allowing several clients to communicate with a single server.
 * This also houses all the logic for parsing the incoming commands from the client.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
public class ClientThread implements Runnable {
    //region Properties
    private final String END_TRANSMISSION = "END_TRANSMISSION";
    private final String END_CONNECTION = "END_CONNECTION";
    private CommandParser cmdParser;
    private Socket clientSocket;
    private DaoDelegator dao;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private boolean running;
    //endregion

    //region Constructor
    public ClientThread(Socket clientSocket, ConnectionManager connectionManager) {
        this.clientSocket = clientSocket;
        dao = new DaoDelegator(connectionManager);
        openStreams();
    }
    //endregion

    //region Listener
    @Override
    public void run() {
        running = true;
        cmdParser = new CommandParser();

        String msg = "@|green You have been successfully connected to the Scheduler Database.\n" +
                "To search the database, type \"search (lecturer|subject) <search term>\"|@\n";
        sendMsgToClient(msg);
        sendMsgToClient(END_TRANSMISSION);

        while (running) {
            msg = receiveMsgFromClient();
            runCmd(msg);
        }
    }
    //endregion

    //region Stream Management

    /**
     * Opens the streams to communicate with the client.
     */
    private void openStreams() {
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Could not open IO streams for the client.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Closes the streams and socket.
     */
    private void closeStreams() {
        try {
            System.out.println("Closing streams and terminating thread.");
            running = false;
            sendMsgToClient(END_CONNECTION);
            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //endregion

    //region Client Communication

    /**
     * Receive a message from the client.
     * @return String containing the message from the client
     */
    private String receiveMsgFromClient() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            System.err.println("Client is gone.");
            System.err.println(e.getMessage());
            running = false;
            return "";
        }
    }

    /**
     * Send a message to the client.
     * @param msg Message to send to the client
     */
    private void sendMsgToClient(String msg) {
        try {
            outputStream.writeUTF(msg);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Could not send message to the client.");
            System.err.println(e.getMessage());
        }
    }
    //endregion

    //region Command Management


    /**
     * Run the input from the client through the command parser,
     * and run the correct function for the command.
     * @param input String containing the command to be ran
     */
    private void runCmd(String input) {
        CommandDetails cmd = cmdParser.parse(input);
        if (cmd != null) {
            switch (cmd.getAction()) {
                case LIST:
                    listAll(cmd.getType());
                    break;
                case SEARCH:
                    search(cmd);
                    break;
                case SEND_HELP:
                    sendHelp(cmd);
                    break;
                case SEND_USAGE:
                    sendUsage(cmd.getType());
                    break;
                case EXIT:
                    closeStreams();
                    break;
            }
        }
        sendMsgToClient(END_TRANSMISSION);
    }

    /**
     * Used to determine which list command to run,
     * based on the type.
     * @param type CommandType to run list command for
     */
    private void listAll(CommandType type) {
        switch (type) {
            case LECTURER:
                listLecturer();
                break;
            case SUBJECT:
                listSubject();
                break;
        }
    }

    /**
     * Sends a list of lecturers to the client.
     */
    private void listLecturer() {
        try {
            List<LecturerDTO> lecturerDTOS = dao.getLecturerDao().queryForAll();
            sendTableHeader(CommandType.LECTURER);
            for (LecturerDTO lecturerDTO : lecturerDTOS) {
                sendLecturer(lecturerDTO);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            sendMsgToClient("Failed to retrieve list of lecturers.");
        }
    }

    /**
     * Sends a list of subjects to the client.
     */
    private void listSubject() {
        try {
            List<SubjectDTO> subjectDTOS = dao.getSubjectDao().queryForAll();
            sendTableHeader(CommandType.SUBJECT);
            for (SubjectDTO subjectDTO : subjectDTOS) {
                sendSubject(subjectDTO);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            sendMsgToClient("Failed to retrieve list of subjects.");
        }
    }

    /**
     * Used to run the correct search command based on the CommandType and arguments.
     * @param cmd CommandDetail containing type and arguments
     */
    private void search(CommandDetails cmd) {
        String argsAsString = cmd.getArgs().stream().collect(Collectors.joining(" "));
        switch (cmd.getType()) {
            case LECTURER:
                searchLecturer(argsAsString);
                break;
            case SUBJECT:
                searchSubject(argsAsString);
                break;
        }
    }

    /**
     * Searches the database for the given lecturer and send it to the client.
     * @param lecturer The lecturer to find
     */
    private void searchLecturer(String lecturer) {
        LecturerDTO lecturerDTO = dao.getLecturerDao().queryForExactOrPartialName(lecturer);
        sendTableHeader(CommandType.LECTURER);
        if (lecturerDTO != null) {
            sendLecturer(lecturerDTO);
        } else {
            sendMsgToClient("No result was found.");
        }
    }

    /**
     * Searches the database for the given subject and send it to the client.
     * @param subject The subject to find
     */
    private void searchSubject(String subject) {
        SubjectDTO subjectDTO = dao.getSubjectDao().findSubjectByCodeOrName(subject);
        sendTableHeader(CommandType.SUBJECT);
        if (subjectDTO != null) {
            sendSubject(subjectDTO);
        } else {
            sendMsgToClient("No result was found.");
        }
    }

    //endregion

    //region Senders

    /**
     * Used to send the table header for a specific type of item.
     * @param type The type to send usage for
     */
    private void sendTableHeader(CommandType type) {
        switch (type) {
            case LECTURER:
                sendMsgToClient("@|bold,cyan " + String.format("%-25s %s", "Name", "Subject" + "|@"));
                sendMsgToClient("@|cyan -------------------------------- |@");
                break;
            case SUBJECT:
                sendMsgToClient("@|bold,cyan " +
                        String.format("%-30s %-10s %-10s %s", "Subject", "Code", "Enrolled", "Lecturer(s)|@"));
                sendMsgToClient("@|cyan ----------------------------------------------------------------------------- |@");
                break;
        }
    }

    /**
     * Send given item to client in formatted format.
     * @param subjectDTO The subject to extract information from
     */
    private void sendSubject(SubjectDTO subjectDTO) {
        if (subjectDTO != null) {

            // bad hack to avoid nullpointer exception
            if (subjectDTO.getLecturers().size() == 0) {
                subjectDTO.getLecturers().add(null);
            }

            sendMsgToClient(
                    String.format("%-30s %-10s %-10d %s",
                            subjectDTO.getName(),
                            subjectDTO.getShortName(),
                            subjectDTO.getEnrolled(),
                            subjectDTO.getLecturers().get(0) != null ?
                                    subjectDTO.getLecturers().get(0).getName() : "None"
                    )
            );


            if (subjectDTO.getLecturers().size() > 1) {
                for (int i = 1; i < subjectDTO.getLecturers().size(); i++) {
                    sendMsgToClient(
                            String.format("%-30s %-10s %-10s %s",
                                    "", "", "",
                                    subjectDTO.getLecturers().get(i) != null ?
                                            subjectDTO.getLecturers().get(i).getName() : ""
                            )
                    );
                }
            }
        }
    }

    /**
     * Send given item to client in formatted format.
     * @param lecturerDTO The lecturer to extract information from
     */
    private void sendLecturer(LecturerDTO lecturerDTO) {
        if (lecturerDTO != null) {
            List<SubjectDTO> subjects = new ArrayList<>();
            try {
                subjects = dao.getLecturerDao().findSubjectsForLecturer(lecturerDTO);
            } catch (SQLException e) {
                System.err.println("Query failed.");
                System.err.println(e.getMessage());
            }

            // hack to avoid nullpointer later
            if (subjects.size() == 0) {
                subjects.add(null);
            }

            sendMsgToClient(
                    String.format("%-25s %s",
                            lecturerDTO.getName(),
                            subjects.get(0) != null ? subjects.get(0).getShortName() : "None"
                    )
            );

            if (subjects.size() < 2)
                return;

            for (int i = 1; i < subjects.size(); i++) {
                sendMsgToClient(
                        String.format("%-25s %s",
                                "",
                                subjects.get(i) != null ? subjects.get(i).getShortName() : ""
                        )
                );
            }
        }
    }

    /**
     * Send usage for a specific command.
     * @param type The command to send usage for
     */
    private void sendUsage(CommandType type) {
        switch (type) {
            case LIST:
                sendMsgToClient("@|bold,blue Usage:|@ @|blue list lecturer|subject|@");
                break;
            case SEARCH:
                sendMsgToClient("@|bold,blue Usage:|@ @|blue search (lecturer|subject|room) <search term>|@");
                break;
        }
    }

    /**
     * Sends help content for given command.
     *
     * @param cmd Command to send help for
     */
    private void sendHelp(CommandDetails cmd) {
        switch (cmd.getType()) {
            case LIST:
                sendMsgToClient("@|bold,cyan List:|@");
                sendMsgToClient("@|cyan -------------------------------|@");
                sendUsage(CommandType.LIST);
                sendMsgToClient("The list command is used to list everything about a single item.");
                sendMsgToClient("You can use it to view all lecturers, subjects or rooms.");
                sendMsgToClient("@|bold,magenta Usage examples:|@");
                sendMsgToClient("list lecturer");
                sendMsgToClient("@|cyan -------------------------------|@");
                break;
            case SEARCH:
                sendMsgToClient("@|bold,cyan Search:|@");
                sendMsgToClient("@|cyan -------------------------------|@");
                sendUsage(CommandType.SEARCH);
                sendMsgToClient("The search command is used to find information.");
                sendMsgToClient("You can use search with lecturer name or subject code.");
                sendMsgToClient("@|bold,magenta Usage examples:|@");
                sendMsgToClient("search lecturer Praskovya Pokrovskaya");
                sendMsgToClient("search subject PGR200");
                sendMsgToClient("@|cyan -------------------------------|@\n");
                break;
            case NONE:
                if (cmd.getArgs().size() > 0) {
                    sendMsgToClient("@|red The command '" + cmd.getArgs().get(0) + "' does not exist.|@\n");
                } else {
                    sendMsgToClient("@|bold,cyan Help:|@");
                    sendMsgToClient("@|cyan ----------------------------|@");
                    sendMsgToClient("@|magenta The following commands are available.|@");
                    sendMsgToClient("@|magenta For more information, type \"help <cmd>\".|@");
                    sendMsgToClient("@|red search|@");
                    sendMsgToClient("@|red list|@");
                    sendMsgToClient("@|red exit|@");
                }
                break;
        }
    }

    //endregion
}
