package no.kij.socketscheduler.client;

import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    private final int PORT = 8432;
    private final String END_TRANSMISSION = "END_TRANSMISSION";
    private final String END_CONNECTION = "END_CONNECTION";
    private Socket server;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Scanner scanner;
    private boolean running;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Main main = new Main();
        main.startClient();

        System.out.println(String.format("%-25s %s",
                "Name", "Subject"));
        System.out.println(String.format("%-25s %s",
                "Praskovya Pokrovskaya", "Avansert Javaprogrammering"));
    }



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
                    System.out.println(">>> ");
                    sendMsgToServer(scanner.nextLine());
                }
            }

            System.out.println("Client shutdown.");

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
            System.err.println("Something went wrong while sending the message to server.");
            System.err.println(e.getMessage());
        }
    }

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
