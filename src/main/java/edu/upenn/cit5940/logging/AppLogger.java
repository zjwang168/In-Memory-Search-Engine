package edu.upenn.cit5940.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AppLogger {
    private static final AppLogger INSTANCE = new AppLogger();
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private BufferedWriter writer;

    private AppLogger() {
    }

    public static AppLogger getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize(String logFilePath) throws IOException {
        String pathString = (logFilePath == null || logFilePath.isBlank())
                ? "tech_news_search.log"
                : logFilePath;

        Path path = resolvePath(pathString);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (writer != null) {
            writer.close();
        }

        writer = Files.newBufferedWriter(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    private synchronized void log(String level, String message) {
        if (writer == null) {
            return;
        }
        String ts = LocalDateTime.now().format(TS_FORMAT);
        String line = "[" + ts + "] " + level + " " + message;
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException ignored) {
            // Keep the app resilient even if logging fails.
        }
    }

    private Path resolvePath(String configuredPath) {
        Path configured = Paths.get(configuredPath);
        if (configured.isAbsolute()) {
            return configured;
        }
        return Paths.get("").toAbsolutePath().resolve(configured).normalize();
    }
}
