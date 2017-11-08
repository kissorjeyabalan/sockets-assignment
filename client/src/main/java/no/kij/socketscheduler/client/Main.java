package no.kij.socketscheduler.client;

import no.kij.socketscheduler.common.util.ResourceFetcher;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    private final int PORT = 8432;
    private final String KILL_HANGUP = "END_TRANSMISSION";
    private Socket server;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Scanner scanner;
    private boolean running;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Main main = new Main();
        main.startClient();
    }

    private void startClient() {
        System.out.println("Attempting to open connection...");
        try {
            running = true;
            scanner = new Scanner(System.in);
            server = new Socket("127.0.0.1", PORT);

            System.out.println("Connection opened!\n");
            openStreams();

            do {
                receiveMsgFromServer();
                System.out.println(">>> ");
                sendMsgToServer(scanner.nextLine());
            } while (running);

        } catch (IOException e) {
            System.err.println("Could not establish connection to the server.");
            System.err.println(e.getMessage());
        }
    }

    private void sendMsgToServer(String msg) {
        try {
            outputStream.writeUTF(msg);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Something went wrong while sending the message to client.");
            System.err.println(e.getMessage());
        }
    }

    private void receiveMsgFromServer() {
        try {
            String msg;
            do {
                msg = inputStream.readUTF();
                if (!msg.equals(KILL_HANGUP))
                    System.out.println(ansi().render(msg));
            } while (!msg.equals(KILL_HANGUP));
        } catch (IOException e) {
            System.err.println("Could not receive msg from server.");
            System.err.println(e.getMessage());
        }
    }

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
