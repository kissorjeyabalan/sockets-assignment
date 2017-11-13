package no.kij.socketscheduler.client;

import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * This is the client that is meant to connect to the server.
 * It accepts input from the user and sends it to the server, then listens to incoming messages
 * from the server.
 */
public class Main {
    private final int PORT = 8432;
    private final String END_TRANSMISSION = "END_TRANSMISSION";
    private final String END_CONNECTION = "END_CONNECTION";
    private Socket server;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Scanner scanner;
    private boolean running;

    /**
     * Entry point for the program.
     * It creates an instance of itself and start the client.
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Main main = new Main();
        main.startClient();
    }

    /**
     * This method opens a connection to the server and constantly listens for a message from the server,
     * as well as sends messages to the server until connection is ended.
     */
    private void startClient() {
        System.out.println("Attempting to open connection...");
        try {
            running = true;
            scanner = new Scanner(System.in);
            server = new Socket("127.0.0.1", PORT);

            System.out.println("Connection opened!\n");
            openStreams();

            while (running) {
                receiveMsgFromServer();
                if (running) {
                    sendMsgToServer(scanner.nextLine());
                }
            }

            System.out.println("Client shutdown.");

        } catch (IOException e) {
            System.err.println("Could not establish connection to the server.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sends a message to the server and flushes the stream.
     * @param msg Message to be sent to server
     */
    private void sendMsgToServer(String msg) {
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
    private void receiveMsgFromServer() {
        try {
            String msg;
            do {
                msg = inputStream.readUTF();
                if (msg.equals(END_CONNECTION)) {
                    running = false;
                    return;
                }
                if (!msg.equals(END_TRANSMISSION)) {
                    System.out.println(ansi().render(msg));
                }
            } while (!msg.equals(END_TRANSMISSION));
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
}
