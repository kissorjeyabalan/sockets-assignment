package no.kij.socketscheduler.server.util;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * This class consists exclusively of static methods that is used to fetch information from the resources folder.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
public class ResourceFetcher {

    /**
     * Attempts to find the given file in the resources path.
     *
     * @param fileName Name of file to be read, file extension inclusive
     * @return Content of file as string. Returns empty string if file is empty
     */
    public static String getFile(String fileName) {
        InputStream in = ResourceFetcher.class.getClassLoader().getResourceAsStream(fileName);
        return getFile(in);
    }

    /**
     * An alternative to getFile with file name.
     * @see ResourceFetcher#getFile(String)
     * @param inputStream InputStream to the file to be retrieved
     * @return String containing the content of the inputStream
     */
    public static String getFile(InputStream inputStream) {
        String content = "";
        try {
            if (inputStream != null) {
                content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            } else {
                return null;
            }
        } catch (IOException e) {
            System.err.println("Something went wrong when converting the file to a string.");
            System.err.println(e.getMessage());
        }
        return content;
    }

    /**
     * Returns properties file from the given file name.
     *
     * @return Properties from the given filename
     * @param fileName String containing name of file containing the properties
     */
    public static Properties getProperty(String fileName) {
        InputStream inputStream = ResourceFetcher.class.getClassLoader().getResourceAsStream(fileName + ".properties");
        return getProperty(inputStream);
    }

    /**
     * An alternative to getProperty using file name.
     * @see ResourceFetcher#getProperty(String)
     * @param inputStream InputStream to fetch properties from
     * @return Properties file
     */
    public static Properties getProperty(InputStream inputStream) {
        Properties prop = new Properties();
        try {
            prop.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Properties could not be loaded.");
            System.err.println(e.getMessage());
            return null;
        }
        return prop;
    }
}
