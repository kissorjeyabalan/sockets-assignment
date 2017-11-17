package no.kij.socketscheduler.server;

import no.kij.socketscheduler.server.db.DatabaseInitializer;
import no.kij.socketscheduler.server.util.ConnectionManager;
import no.kij.socketscheduler.server.util.ResourceFetcher;
import org.h2.tools.Server;
import org.junit.*;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ClientThreadTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static ConnectionManager cm;
    private static Server server;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private Socket socket;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "8372", "-tcpAllowOthers").start();
        cm = new ConnectionManager(ResourceFetcher.getProperty("testProps"));
        DatabaseInitializer dbInit = new DatabaseInitializer(cm);
        dbInit.initializeTables();
        dbInit.initializeTableContent();
    }

    @AfterClass
    public static void tearDownClass() {
        server.stop();
    }

    @Before
    public void setUp() throws IOException {
        System.setErr(new PrintStream(errContent));
        socket = mock(Socket.class);

        // We want the mocked socket to return a ByteArrayStream
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream(createUTFMessage("exit"));

        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(inputStream);
    }

    @After
    public void tearDown() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.err)));
    }

    @Test
    public void testRunStartsClientLoop() throws IOException {
        // testing if thread closes properly on exit msg from client
        runWithMsg("exit");
        assertTrue(outputStream.toString().contains("You have been successfully connected to the Scheduler Database."));
        assertTrue(outputStream.toString().contains("END_TRANSMISSION"));
    }

    @Test
    public void testRunClosesOnMsgReceiveFailure() throws IOException {
        // since we're not updating inputstream between loops,
        // the stream should throw a exception since msg can't be read.
        runWithMsg("failure");

        assertTrue(errContent.toString().contains("Client is gone."));

    }

    @Test
    public void testServerRespondsToClientCorrectly() throws IOException {
        runWithMsg("help");
        String output = outputStream.toString();

        assertTrue(output.contains("Help:"));
        assertTrue(output.contains("The following commands are available."));
    }

    @Test
    public void testListLecturer() throws IOException {
        runWithMsg("list lecturer");
        String output = outputStream.toString();

        assertTrue(output.contains("Name"));
        assertTrue(output.contains("Alexander"));
        assertTrue(output.contains("Vilde"));
        assertTrue(output.contains("Lilly"));
    }

    @Test
    public void testListSubject() throws IOException {
        runWithMsg("list subject");
        String output = outputStream.toString();

        assertTrue(output.contains("Subject"));
        assertTrue(output.contains("PGR200"));
        assertTrue(output.contains("PG4200"));
        assertTrue(output.contains("PG3300"));
    }

    @Test
    public void testListUsage() throws IOException {
        runWithMsg("list asdg");

        String output = outputStream.toString();
        assertTrue(output.contains("Usage:"));
        assertTrue(output.contains("list lecturer|subject"));
    }

    @Test
    public void testSearchLecturer() throws IOException {
        runWithMsg("search lecturer vilde");
        String output = outputStream.toString();

        assertTrue(output.contains("No result was found."));
    }

    @Test
    public void testSearchSubject() throws IOException {
        runWithMsg("search subject pgr200asdf");
        String output = outputStream.toString();

        assertTrue(output.contains("No result was found."));
    }

    @Test
    public void testListHelp() throws IOException {
        runWithMsg("help list");
        String output = outputStream.toString();
        assertTrue(output.contains("You can use it to view all lecturers, subjects or rooms."));
    }

    @Test
    public void testSearchHelp() throws IOException {
        runWithMsg("help search");
        String output = outputStream.toString();
        assertTrue(output.contains("You can use search with lecturer name or subject code."));
    }

    @Test
    public void testWrongCommandMsg() throws IOException {
        runWithMsg("help asdf");
        String output = outputStream.toString();
        assertTrue(output.contains("The command 'asdf' does not exist."));
    }


    private void runWithMsg(String msg) throws IOException {
        when(socket.getInputStream()).thenReturn(
                new ByteArrayInputStream(
                        createUTFMessage(msg)
                )
        );

        ClientThread ct = new ClientThread(socket, cm);
        ct.run();

    }

    private byte[] createUTFMessage(String message) {
        // create a byte array with our message, and then copy it into a new array.
        // the first two values in the new byte array represents the length of the original msg,
        // since readUTF requires the first two values to tell it how long the message is.
        byte[] msg = (message).getBytes();
        byte[] utfArray;
        utfArray = new byte[msg.length + 2];
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