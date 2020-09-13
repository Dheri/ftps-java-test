import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static final String APPLICATION_PROPERTIES = "application.properties";
    private static Properties props;
    private static final Logger logger = LoggerFactory.getLogger(FTPUploader.class);

    public static void main(String args[]) {
        logger.debug("hello w");
        run();
    }

    private static void run() {
        readProperties();
        try {
            ftp();
        } catch (Exception e) {
            logger.error("Exception :" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ftp() throws Exception {
        String ftpHostname = props.getProperty("FTP_HOSTNAME");
        String ftpUsername = props.getProperty("FTP_USERNAME");
        String ftpPassword = props.getProperty("FTP_PASSWORD");

        FTPUploader ftpUploader = new FTPUploader(ftpHostname, ftpUsername, ftpPassword);
        logger.debug("constructor made");
        String ftpsDirectory = props.getProperty("FTP_DIRECTORY");

        logger.debug("listing files before upload");
        ftpUploader.listDirectory("/", "", 3);

        Long currentTimeMillis = System.currentTimeMillis();
        String uploadFileName = currentTimeMillis.toString();
        uploadFileName += ".txt";
        logger.debug("File name to be uploaded:  " + uploadFileName);

        String localPath = "src/main/resources/test.txt";
        ftpUploader.uploadFile(localPath, uploadFileName, ftpsDirectory);

        logger.debug("listing files after upload");
        ftpUploader.listDirectory("/", "", 3);

        ftpUploader.disconnect();
    }

    private static void readProperties() {
        String resourceName = APPLICATION_PROPERTIES;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
