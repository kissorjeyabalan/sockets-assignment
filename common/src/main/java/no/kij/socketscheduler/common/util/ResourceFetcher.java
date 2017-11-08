package no.kij.socketscheduler.common.util;

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
     * Attempts to find the given filename in the resources path.
     *
     * @param fileName Name of file to be read, file extension inclusive
     * @return Content of file as string. Returns empty string if file is empty
     * @throws IOException Throws exception if file is not found or can not be read
     */
    public static String getFile(String fileName) {
        InputStream in = ResourceFetcher.class.getClassLoader().getResourceAsStream(fileName);
        String content = "";
        try {
            if (in != null) {
                content = IOUtils.toString(in, StandardCharsets.UTF_8);
            } else {
                return null;
            }
        } catch (IOException e) {
            System.err.println("Something went wrong when converting the file " + fileName + " to a string.");
            System.err.println(e.getMessage());
        }
        return content;
    }

    /**
     * Returns properties file from the given filename.
     *
     * @return Properties containing the given filename
     */
    public static Properties getProperty(String propertyName) {
        Properties prop = new Properties();
        try {
            InputStream in = ResourceFetcher.class.getClassLoader().getResourceAsStream(propertyName + ".properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            System.err.println("Properties could not be loaded.");
            System.err.println(e.getMessage());
            return null;
        }
        return prop;
    }
}
