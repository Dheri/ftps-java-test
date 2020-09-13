import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    private static Properties props;

    public static void main(String args[]) {
        System.out.println("hello w");
        run();
    }

    private static void run() {
        readProperties();
        String serverUrl = props.getProperty("server-url");
        System.out.println(serverUrl);
    }

    private static void readProperties() {
        String resourceName = "application.properties"; // could also be a constant
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
