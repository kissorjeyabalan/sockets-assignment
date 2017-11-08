package no.kij.socketscheduler.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.setDetector;

/**
 * A class used to thread incoming client connections to a new thread,
 * allowing several clients to communicate with a single server.
 */
public class ClientThread implements Runnable {
    private final String KILL_HANGUP = "END_TRANSMISSION";
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        openStreams();
    }

    @Override
    public void run() {
        String msg = "@|green You have been successfully connected to the Scheduler Database.\n" +
                "To search the database, type \"search (lecturer|subject) <search term>\"|@\n";
        sendMsgToClient(msg);
        sendMsgToClient("END_TRANSMISSION");
        do {
            msg = receiveMsgFromClient();
            if (!msg.equals(KILL_HANGUP))
                findCmd(msg);
        } while (!msg.equals(KILL_HANGUP));
    }


    private String receiveMsgFromClient() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            System.err.println("Client is gone.");
            System.err.println(e.getMessage());
            return KILL_HANGUP;
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



    private void findCmd(String input) {
        String[] splitInput = input.toLowerCase().split(" ");
        if (splitInput.length > 0) {
            switch (splitInput[0]) {
                case "list":
                    if (splitInput.length > 1) {
                        //listAll(splitInput[1]);
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
                default:
                    sendMsgToClient(KILL_HANGUP);
                    break;
            }
        }
    }

    private void sendUsage(String cmd) {
        switch (cmd) {
            case "list":
                sendMsgToClient("@|bold,blue Usage:|@ @|blue list lecturer|subject|@");
                break;
            case "search":
                sendMsgToClient("@|bold,blue Usage:|@ @|blue search (lecturer|subject|room <search term>|@)");
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
        sendMsgToClient(KILL_HANGUP);
    }
}
