package no.kij.socketscheduler.server;

import com.j256.ormlite.logger.LocalLog;
import no.kij.socketscheduler.common.util.ResourceFetcher;
import no.kij.socketscheduler.server.db.ConnectionManager;
import no.kij.socketscheduler.server.db.DatabaseInitializer;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * This class provides the packaged product an entrypoint,
 * allowing the server to start up and look for connections and thread them accordingly.
 */
public class Main {
    private final int PORT = 8432;
    private ConnectionManager connectionManager;

    public static void main(String[] args) {
        // this is for enabling ansi output to windows cli
        AnsiConsole.systemInstall();
        // since we are using the console for our application, we don't want debug level items ending up as clutter,
        // so we disable all log for debug, except error.
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        Main main = new Main();
        main.startServer();
    }

    public void startServer() {
        Properties creds = ResourceFetcher.getProperty("credentials");
        connectionManager = new ConnectionManager(creds);
        DatabaseInitializer dbIn = new DatabaseInitializer(connectionManager);
        dbIn.initializeTables();
        dbIn.initializeTableContent();

        System.out.println(ansi().render("@|bold,green Server started!|@"));
        try {
            ServerSocket server = new ServerSocket(PORT);
            attemptClientConnection(server);
        } catch (IOException e) {
            System.out.println("Something horribly went wrong.");
            throw new RuntimeException("Server could not be started. " + e.getMessage());
        }
    }

    private void attemptClientConnection(ServerSocket server) throws IOException {
        int client = 0;
        while (true) {
            System.out.println(ansi().render("@|bold,cyan Looking for new connection...|@"));
            Socket clientSocket = server.accept();
            System.out.println(ansi().render("@|cyan Connected to client " + ++client + "!|@"));
            ClientThread clientThread = new ClientThread(clientSocket, connectionManager);
            new Thread(clientThread).start();
            System.out.println(ansi().render("@|cyan Assigned client|@ @|red " + client + "|@ @|cyan a new thread!\n|@"));
        }
    }
}
