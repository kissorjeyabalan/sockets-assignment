package no.kij.socketscheduler.server;

import no.kij.socketscheduler.common.dao.LecturerDao;
import no.kij.socketscheduler.common.dto.LecturerDTO;
import no.kij.socketscheduler.common.dto.SubjectDTO;
import no.kij.socketscheduler.server.db.ConnectionManager;
import no.kij.socketscheduler.server.db.DaoDelegator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * A class used to thread incoming client connections to a new thread,
 * allowing several clients to communicate with a single server.
 * This also houses all the logic for parsing the incoming commands from the client.
 */
public class ClientThread implements Runnable {
    private final String END_TRANSMISSION = "END_TRANSMISSION";
    private final String END_CONNECTION = "END_CONNECTION";
    private Socket clientSocket;
    private DaoDelegator dao;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private boolean running;


    public ClientThread(Socket clientSocket, ConnectionManager connectionManager) {
        this.clientSocket = clientSocket;
        dao = new DaoDelegator(connectionManager);
        openStreams();
    }

    @Override
    public void run() {
        String msg = "@|green You have been successfully connected to the Scheduler Database.\n" +
                "To search the database, type \"search (lecturer|subject) <search term>\"|@\n";
        sendMsgToClient(msg);
        sendMsgToClient(END_TRANSMISSION);
        running = true;

        while (running) {
            msg = receiveMsgFromClient();
            findCmd(msg);
        }
    }


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

    private void sendMsgToClient(String msg) {
        try {
            outputStream.writeUTF(msg);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Could not send message to the client.");
            System.err.println(e.getMessage());
        }
    }

    private void openStreams() {
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Could not open IO streams for the client.");
            System.err.println(e.getMessage());
        }
    }

    private void closeThread() {
        try {
            System.out.println("Closing streams and terminating thread.");
            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    private void sendTableHeader(String header) {
        switch (header) {
            case "lecturer":
                sendMsgToClient("@|bold,cyan " + String.format("%-25s %s", "Name", "Subject" + "|@"));
                sendMsgToClient("@|cyan -------------------------------- |@");
                break;
            case "subject":
                sendMsgToClient("@|bold,cyan " +
                        String.format("%-30s %-10s %-10s %s", "Subject", "Code", "Enrolled", "Lecturer(s)|@"));
                sendMsgToClient("@|cyan ----------------------------------------------------------------------------- |@");
                break;
        }
    }

    private void listAll(String arg) {
        try {
            switch (arg) {
                case "lecturer":
                    List<LecturerDTO> lecturerDTOs = dao.getLecturerDao().queryForAll();
                    sendTableHeader("lecturer");
                    for (LecturerDTO lecturerDTO : lecturerDTOs) {
                        sendLecturer(lecturerDTO);
                    }
                    sendMsgToClient("");
                    break;
                case "subject":
                    List<SubjectDTO> subjectDTOs = dao.getSubjectDao().queryForAll();
                    sendTableHeader("subject");
                    for (SubjectDTO subjectDTO : subjectDTOs) {
                        sendSubject(subjectDTO);
                    }
                    sendMsgToClient("");
                    break;
            }
        } catch (SQLException e) {
            System.err.println("Couldn't fetch lecturers.");
            System.err.println(e.getMessage());
            sendMsgToClient("Failed to retrieve list of lecturers.");
            sendMsgToClient(END_TRANSMISSION);
        }
        sendMsgToClient(END_TRANSMISSION);
    }


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

    private void sendLecturer(LecturerDTO lecturerDTO) {
        if (lecturerDTO != null) {
            List<SubjectDTO> subjects = new ArrayList<>();
            try {
                subjects = dao.getSubjectDao().findSubjectsForLecturer(lecturerDTO);
            } catch (SQLException e) {
                System.err.println("Query failed.");
                System.err.println(e.getMessage());
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


    private void findCmd(String input) {
        String[] splitInput = input.toLowerCase().split(" ");
        if (splitInput.length > 0) {
            switch (splitInput[0]) {
                case "list":
                    if (splitInput.length > 1) {
                        listAll(splitInput[1]);
                    } else {
                        sendUsage("list");
                    }
                    break;
                case "search":
                    if (splitInput.length > 2) {
                        List<String> refinedInput = new ArrayList<>();
                        for (int i = 1; i < splitInput.length; i++) {
                            refinedInput.add(splitInput[i]);
                        }
                        //search(refinedInput);
                    } else {
                        sendUsage("search");
                    }
                    break;
                case "help":
                    if (splitInput.length > 1) {
                        sendHelp(splitInput[1]);
                    } else {
                        sendHelp();
                    }
                    break;
                case "exit":
                    running = false;
                    sendMsgToClient("Goodbye.");
                    sendMsgToClient(END_CONNECTION);
                    closeThread();
                    break;
            }
        }
        sendMsgToClient(END_TRANSMISSION);
    }

    private void sendUsage(String cmd) {
        switch (cmd) {
            case "list":
                sendMsgToClient("@|bold,blue Usage:|@ @|blue list lecturer|subject|@");
                break;
            case "search":
                sendMsgToClient("@|bold,blue Usage:|@ @|blue search (lecturer|subject|room) <search term>|@");
                break;
        }
    }

    private void sendHelp(String... cmd) {
        if (cmd.length > 0) {
            switch (cmd[0]) {
                case "search":
                    sendMsgToClient("@|bold,cyan Search:|@");
                    sendMsgToClient("@|cyan -------------------------------|@");
                    sendUsage("search");
                    sendMsgToClient("The search command is used to find information.");
                    sendMsgToClient("You can use search with lecturer name or subject code.");
                    sendMsgToClient("@|bold,magenta Usage examples:|@");
                    sendMsgToClient("search lecturer Praskovya Pokrovskaya");
                    sendMsgToClient("search subject PGR200");
                    sendMsgToClient("@|cyan -------------------------------|@\n");
                    break;
                case "list":
                    sendMsgToClient("@|bold,cyan List:|@");
                    sendMsgToClient("@|cyan -------------------------------|@");
                    sendUsage("list");
                    sendMsgToClient("The list command is used to list everything about a single item.");
                    sendMsgToClient("You can use it to view all lecturers, subjects or rooms.");
                    sendMsgToClient("@|bold,magenta Usage examples:|@");
                    sendMsgToClient("list lecturer");
                    sendMsgToClient("@|cyan -------------------------------|@");
                    break;
                default:
                    sendMsgToClient("@|red The command '" + cmd[0] + "' does not exist.|@\n");
                    break;
            }
        } else {
            sendMsgToClient("@|bold,cyan Help:|@");
            sendMsgToClient("@|cyan ----------------------------|@");
            sendMsgToClient("@|magenta The following commands are available.|@");
            sendMsgToClient("@|magenta For more information, type \"help <cmd>\".|@");
            sendMsgToClient("@|red search|@");
            sendMsgToClient("@|red list|@");
        }
    }
}