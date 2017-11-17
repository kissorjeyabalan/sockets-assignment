package no.kij.socketscheduler.server.util;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

public class ResourceFetcherTest {
    @Test
    public void testGetFileSuccess() {
        String string;
        string = ResourceFetcher.getFile("test.txt");
        assertNotNull(string);
        assertEquals("test content", string);
    }

    @Test
    public void testGetFileIOExceptionBlock() throws IOException {
        String string = null;
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.txt");
        in.close();
        string = ResourceFetcher.getFile(in);
        assertEquals("", string);
    }

    @Test
    public void testGetFileInvalidFile() throws IOException {
        InputStream nullInputStream = null;
        String string = ResourceFetcher.getFile(nullInputStream);
        assertNull(string);
    }

    @Test
    public void testGetPropertySuccess() {
        Properties properties;
        properties = ResourceFetcher.getProperty("testProps");
        assertNotNull(properties);
        assertEquals("jdbc:h2:mem:orm", properties.getProperty("database_url"));
        assertEquals("username", properties.getProperty("username"));
        assertEquals("password", properties.getProperty("password"));
    }

    @Test
    public void testGetPropertyIOExceptionBlock() throws IOException {
        Properties properties = null;
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.txt");
        in.close();
        properties = ResourceFetcher.getProperty(in);
        assertNull(properties);
    }

}