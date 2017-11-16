package no.kij.socketscheduler.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.InstanceOf;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private Socket socket;

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        socket = mock(Socket.class);

        // We want the mocked socket to return a ByteArrayStream
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream(createUTFMessage("END_TRANSMISSION"));

        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(inputStream);
    }

    @After
    public void tearDown() {
        socket = null;
        outputStream = null;
        inputStream = null;
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }


    @Test
    public void testSendMsgToClientSendsMessage() {
        Client client = createClient();
        String msg = "test msg";
        client.sendMsgToServer(msg);

        String filteredWrittenFirstMsg = removeUTF(outputStream.toByteArray());

        assertEquals(msg, filteredWrittenFirstMsg);
    }

    @Test
    public void testReceiveMsgFromServer() throws IOException {
        byte[] msg = createUTFMessage("server response END_TRANSMISSION");
        // return the new message to the client when it tries to read
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(msg));

        Client client = createClient();
        client.receiveMsgFromServer();

        // check that our msg was printed to system out
        String filteredMsg = removeUTF(msg);
        assertTrue(outContent.toString().contains(filteredMsg));
    }

    @Test
    public void testReceiveMsgFromServerShutsDownClient() throws IOException {
        byte[] msg = createUTFMessage("END_CONNECTION");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(msg));

        Client client = createClient();
        client.receiveMsgFromServer();

        assertTrue(outContent.toString().contains("Server disconnected."));
    }

    @Test
    public void testStartClientBreaksLoop() throws IOException {
            byte[] msg = createUTFMessage("END_CONNECTION");
            when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(msg));

            Client client = createClient();
            client.startClient();

            assertTrue(outContent.toString().contains("Client shutdown."));
    }

    @Test (expected = java.net.ConnectException.class)
    public void testCreateSocketThrowsConnectionException() throws IOException {
        Client client = new Client();
        client.createSocket(null, 1);
    }

    private Client createClient() {
        // Create a client and replace the createSocket method, so it returns the mocked socket instead
        return new Client() {
            @Override
            protected Socket createSocket(String host, int port) throws IOException {
                return socket;
            }
        };
    }

    private byte[] createUTFMessage(String message) {
        // create a byte array with our message, and then copy it into a new array.
        // the first two values in the new byte array represents the length of the original msg,
        // since readUTF requires the first two values to tell it how long the message is.
        byte[] msg = (message).getBytes();
        byte[] utfArray = new byte[msg.length + 2];
        System.arraycopy(msg, 0, utfArray, 2, msg.length);
        utfArray[1] = (byte) msg.length;
        return utfArray;
    }

    private String removeUTF(byte[] msg) {
        // Get the message as a byte array and then strip the first two bytes,
        // (since it's added by writeUTF to tell how long the message is)
        return new String(Arrays.copyOfRange(msg, 2, msg.length));
    }
}