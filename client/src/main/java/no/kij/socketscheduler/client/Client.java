package no.kij.socketscheduler.client;

import org.fusesource.jansi.AnsiConsole;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * The client class is responsible for starting the client and establishing a connection to the server.
 * It allows the user to communicate with the server using text input.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
public class Client {
    private final int PORT = 8432;
    private final String HOST = "127.0.0.1";
    private final static String END_TRANSMISSION = "END_TRANSMISSION";
    private final static String END_CONNECTION = "END_CONNECTION";
    private Socket server;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Scanner scanner;
    private boolean running;

    /**
     * Entry point for the program.
     */
    public Client () {
        AnsiConsole.systemInstall();
        setupClient();
    }

    /**
     * This method opens a connection to the server and constantly listens for a message from the server,
     * as well as sends messages to the server until connection is ended.
     */
    public void startClient() {
            while (running) {
                receiveMsgFromServer();
                if (running) {
                    sendMsgToServer(scanner.nextLine());
                }
            }

            System.out.println("Client shutdown.");
    }

    /**
     * Sends a message to the server and flushes the stream.
     * @param msg Message to be sent to server
     */
    public void sendMsgToServer(String msg) {
        try {
            outputStream.writeUTF(msg);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Something went wrong while sending the message to server.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Receives a message from the server,
     * if its told to terminate by the server,
     * it relays the message to terminate to the client.
     */
    public void receiveMsgFromServer() {
        try {
            String msg;
            do {
                msg = inputStream.readUTF();
                if (msg.equals(END_CONNECTION)) {
                    running = false;
                    System.out.println("Server disconnected.");
                    return;
                }
                if (!msg.equals(END_TRANSMISSION)) {
                    System.out.println(ansi().render(msg));
                }
            } while (!msg.contains(END_TRANSMISSION));
        } catch (IOException e) {
            System.err.println("Could not receive msg from server.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Opens the necessary streams to communicate with the sever.
     */
    private void openStreams() {
        try {
            outputStream = new DataOutputStream(server.getOutputStream());
            inputStream = new DataInputStream(server.getInputStream());
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Something went wrong while listening to the server.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initializes the class fields with the necessary values.
     */
    private void setupClient() {
        try {
            System.out.println("Attempting to open connection...");
            scanner = new Scanner(System.in);
            server = createSocket(HOST, PORT);
            running = true;
            System.out.println("Connection opened!\n");
            openStreams();
        } catch (IOException e) {
            System.err.println("Could not open connection to the server.");
            System.err.println(e.getMessage());
            running = false;
        }
    }

    /**
     * Creates a socket using the given parameters.
     */
    protected Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }
}
