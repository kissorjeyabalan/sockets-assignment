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

    /**
     * Entry point for the program.
     * It creates an instance of itself and start the client.
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.startClient();
    }

}
