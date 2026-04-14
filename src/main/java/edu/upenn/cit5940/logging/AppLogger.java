package edu.upenn.cit5940.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton logger used across the entire application.
 *
 * This class ensures that:
 * - only one logging instance exists (Singleton pattern)
 * - all components write to the same log file
 * - logging format is consistent throughout the application
 *
 * The logger is thread-safe for write operations and designed
 * to fail silently to avoid breaking the main application flow.
 */
public final class AppLogger {

    /** Single global instance (Singleton Pattern) */
    private static final AppLogger INSTANCE = new AppLogger();

    /** Timestamp format used in log entries */
    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Writer used to output logs to file */
    private BufferedWriter writer;

    /**
     * Private constructor to prevent external instantiation.
     */
    private AppLogger() {
    }

    /**
     * Returns the singleton logger instance.
     *
     * @return global AppLogger instance
     */
    public static AppLogger getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the logger with a file path.
     *
     * If no path is provided, a default log file is used.
     * This method also ensures parent directories exist.
     *
     * @param logFilePath path to the log file
     * @throws IOException if file creation fails
     */
    public synchronized void initialize(String logFilePath) throws IOException {
        String pathString = (logFilePath == null || logFilePath.isBlank())
                ? "tech_news_search.log"
                : logFilePath;

        Path path = resolvePath(pathString);

        // Ensure directory exists
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // Close existing writer if re-initialized
        if (writer != null) {
            writer.close();
        }

        // Create new writer (overwrite mode)
        writer = Files.newBufferedWriter(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    /**
     * Logs an informational message.
     *
     * @param message log message
     */
    public void info(String message) {
        log("INFO", message);
    }

    /**
     * Logs a warning message.
     *
     * @param message log message
     */
    public void warn(String message) {
        log("WARN", message);
    }

    /**
     * Logs an error message.
     *
     * @param message log message
     */
    public void error(String message) {
        log("ERROR", message);
    }

    /**
     * Core logging method.
     *
     * Writes a formatted log line with timestamp and level.
     * This method is synchronized to ensure thread safety.
     *
     * Logging failures are ignored to avoid breaking the app.
     *
     * @param level log level (INFO, WARN, ERROR)
     * @param message log message
     */
    private synchronized void log(String level, String message) {
        if (writer == null) {
            return; // Logger not initialized
        }

        String ts = LocalDateTime.now().format(TS_FORMAT);
        String line = "[" + ts + "] " + level + " " + message;

        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException ignored) {
            // Fail silently to keep application resilient
        }
    }

    /**
     * Resolves the log file path.
     *
     * If the path is relative, it is converted to an absolute path
     * based on the current working directory.
     *
     * @param configuredPath user-provided path
     * @return resolved absolute path
     */
    private Path resolvePath(String configuredPath) {
        Path configured = Paths.get(configuredPath);
        if (configured.isAbsolute()) {
            return configured;
        }
        return Paths.get("").toAbsolutePath().resolve(configured).normalize();
    }
}