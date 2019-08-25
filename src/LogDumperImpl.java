import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * LogDumperImpl is the default Logger to be used by the Client Applications.
 * This class takes care of the asynchronous logging details to be used by LogDumper for background processing.
 */
public class LogDumperImpl implements LogDumper {
    private File logFile;
    private String appName;
    private static LogDumper logDumper; // Single instance for each application

    /**
     * Creates or returns the LogDumperImpl Instance to be used by the client.
     * Note: Clients should interact using the LogDumper interface
     * @return the instance of this class
     */
    public static LogDumper getDefaultLogger() {
        if(logDumper == null)
            logDumper = new LogDumperImpl();
        return logDumper;
    }

    /**
     * Constructs and returns the final string to be logged.
     * @param timeStamp The time when the message was logged
     * @param userMsg The user message to be logged
     * @param type The type of message (Infor/Error/..)
     * @return The final constructed string to be logged
     */
    private String constructLog(String timeStamp, String userMsg, String type) {
        userMsg.replace(',', '|');  // replacing log-field delim in user message;
        return new String( timeStamp + "," + appName + "," + type + "," + userMsg);
    }

    /**
     * This method takes care of appending the final string to be logged in the log file.
     * @param outputStream The outputStream for the log file
     * @param timeStamp The time when the message was logged
     * @param userMsg The user message to be logged
     * @param type The type of message (Infor/Error/..)
     * @throws IOException
     */
    private void writeToFile(FileOutputStream outputStream, String timeStamp, String userMsg, String type) throws IOException {
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.append(constructLog(timeStamp, userMsg, type));
        writer.newLine();
        writer.flush();
    }

    /**
     * This method gets an exclusive lock on the log file and logs the user message.
     * @param timeStamp The time when the message was logged
     * @param userMsg The user message to be logged
     * @param type The type of message (Infor/Error/..)
     * @throws IOException
     */
    private void lockAndLogUserMessage(String timeStamp, String userMsg, String type) throws IOException {
        FileChannel fileChannel = new RandomAccessFile(logFile, "rw").getChannel(); // open file for read/write
        FileLock lock = fileChannel.lock(); // get an exclusive lock
        writeToFile(new FileOutputStream(logFile, true), timeStamp, userMsg, type);
        lock.release();
    }

    /**
     * This method takes care of handling information logging asynchronously.
     * @param userMsg: The user message to be logged
     */
    @Override
    public void info(String userMsg) {
        CompletableFuture.runAsync(() -> {
            try {
                // Note: The time-stamp is created here itself as the order of logging is not known for async methods
                lockAndLogUserMessage(new Date().toString(), userMsg, Constants.INFO_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method takes care of handling error logging asynchronously.
     * @param userMsg: The user message to be logged
     */
    @Override
    public void error(String userMsg) {
        CompletableFuture.runAsync(() -> {
            try {
                lockAndLogUserMessage(new Date().toString(), userMsg, Constants.ERROR_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method must be called by the client before logging. It takes care of creating the log file for the client.
     * Note: The client must make sure that no other application is using the same name!
     * @param appName: The application name for registering the app to LogDumper
     * @return true on successful registration
     */
    @Override
    public boolean registerApp(String appName) {
        this.appName = appName;
        final Path path = Paths.get(Constants.LOG_DIR_PATH + appName + Constants.LOG_EXT);
        try {
            if (Files.notExists(path)) Files.createFile(path);
            logFile = path.toFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
