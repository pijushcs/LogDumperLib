/**
 * The LogDumper Interface is used by the client to interact with LogDumper.
 * LogDumper currently has one default implementation that takes care of the asynchronous logging details.
 */
public interface LogDumper {
    /**
     * This method takes care of logging information logs
     * @param userMsg: The user message to be logged
     */
    void info(String userMsg);

    /**
     * This method takes care of logging error messages
     * @param userMsg: The user message to be logged
     */
    void error(String userMsg);

    /**
     * The client application must register itself with a valid name to be logged.
     * @param appName: The application name for registering the app to LogDumper
     * @return true if the registration was successful
     */
    boolean registerApp(String appName);
}
