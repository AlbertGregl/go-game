package hr.gregl.gogame.game.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogUtil {
    public static final String infoLogMsg1 = "Server is running...";
    public static final String infoLogMsg2 = "Client is running...";
    public static final String infoLogMsg3 = "Server check: No server running on port ";
    public static final String infoLogMsg4 = "Server check: Found a server running on port ";
    public static final String infoLogMsg5 = "Client successfully connected to server at ";
    public static final String infoLogMsg6 = "Server successfully started on port ";
    public static final String infoLogMsg7 = "Client connected to server on port ";
    public static final String infoLogMsg8 = "Game state received: ";
    public static final String infoLogMsg9 = "Placing stone at: ";
    public static final String warningLogMsg1 = "Server already running.";
    public static final String errorLogMsg1 = "Inconsistent board size in received game state.";
    public static final String debugLogMsg1 = "handleCellClick: Click event received by user: ";
    public static final String debugLogMsg2 = "Sending game state. Current player before sending: ";
    public static final String debugLogMsg3 = "Loading received game state";
    public static final String debugLogMsg4 = "Invalid move by ";
    public static final String debugLogMsg8 = "Updated board state: ";
    public static final String debugLogMsg9 = "Current player after update: ";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final String errorLogFile = "error_log.txt";
    private static final String infoLogFile = "info_log.txt";
    private static final String warningLogFile = "warning_log.txt";
    private static final String failedMsg = "Failed to write to log file: ";


    public static void logError(Exception e) {
        String message = e.getMessage();
        log("ERROR", message, errorLogFile);
        e.printStackTrace(System.err);
    }
    public static void logError(String message) {
        log("ERROR", message, errorLogFile);
    }

    public static void logInfo(String message) {
        log("INFO", message, infoLogFile);
    }

    public static void logWarning(String message) {
        log("WARNING", message, warningLogFile);
    }

    public static void logDebug(String message) {
        log("DEBUG", message, infoLogFile);
    }

    private static void log(String level, String message, String fileName) {
        String timestamp = dtf.format(LocalDateTime.now());
        String logMessage = "[" + level + "] " + timestamp + ": " + message;

        System.out.println(logMessage);

        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println(logMessage);
        } catch (IOException e) {
            System.err.println(failedMsg + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

}
