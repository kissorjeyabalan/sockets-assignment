package no.kij.socketscheduler.client;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MainTest {

    @Test
    public void mainStartsClient() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        String[] args = {};
        Main.main(args);

        // we're checking for connection failure, as we know the
        // connection will fail immediately.
        assertTrue(errContent.toString().contains("Could not open connection to the server."));
    }
}