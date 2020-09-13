
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUploader {
    private static final Logger logger = LoggerFactory.getLogger(FTPUploader.class);

    FTPSClient ftp = null;

    public FTPUploader(String host, String user, String pwd) throws Exception {
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("ftps.protocols", "TLSv1.2");
        ftp = new FTPSClient("TLS", true);
        int reply;

        ftp.connect(host);
        System.out.println("Connected to ftp server");
        System.out.println("1" + ftp.getReplyString());
        System.out.println("2=" + Arrays.toString(ftp.getEnabledProtocols()));
        System.out.println("3==" + ftp.getWantClientAuth());
        System.out.println("4===" + ftp.getNeedClientAuth());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(user, pwd);
        System.out.println("3==" + ftp.getWantClientAuth());
        System.out.println("4===" + ftp.getNeedClientAuth());
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try {
            InputStream input = new FileInputStream(new File(localFileFullName));

            logger.info("=================================>" + System.getProperty("ftps.protocols"));
            this.ftp.storeFile(hostDir + fileName, input);
        } catch (IOException e) {
            logger.error("Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already saved to server
                logger.error("Exception : " + f.getMessage());
            }
        }
    }

    public void listDirectory(String parentDir,
                              String currentDir, int level) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = this.ftp.mlistDir(dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".")
                        || currentFileName.equals("..")) {
                    // skip parent directory and directory itself
                    continue;
                }
                for (int i = 0; i < level; i++) {
                    System.out.print("\t");
                }
                if (aFile.isDirectory()) {
                    System.out.println("[" + currentFileName + "]");
                    listDirectory(dirToList, currentFileName, level + 1);
                } else {
                    System.out.println(currentFileName);
                }
            }
        }
    }
}