package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.datamanagement.ArticleDataReader;
import edu.upenn.cit5940.datamanagement.CsvArticleDataReader;
import edu.upenn.cit5940.datamanagement.JsonArticleDataReader;
import edu.upenn.cit5940.logging.AppLogger;
import edu.upenn.cit5940.model.Article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bootstrap utility responsible for application startup data loading.
 *
 * This class centralizes initialization tasks such as:
 * - configuring the logger
 * - resolving file paths
 * - selecting the correct data reader strategy
 * - loading articles
 * - loading stop words
 *
 * Keeping this logic in one place simplifies startup and keeps Main
 * and the processor layer cleaner.
 */
public final class DataBootstrap {
    /**
     * Utility class; should not be instantiated.
     */
    private DataBootstrap() {
    }

    /**
     * Loads articles from the configured input file and initializes logging.
     *
     * The file reader strategy is selected based on the file extension:
     * CSV files use CsvArticleDataReader and JSON files use JsonArticleDataReader.
     *
     * @param dataFile path to the article data file
     * @param logFile path to the application log file
     * @return list of loaded Article objects
     * @throws Exception if startup resources cannot be initialized
     */
    public static List<Article> loadArticles(String dataFile, String logFile) throws Exception {
        AppLogger logger = AppLogger.getInstance();
        logger.initialize(logFile);
        logger.info("Application starting");

        Path dataPath = resolveDataFilePath(dataFile);
        logger.info("Loading articles from " + dataPath);

        String name = dataPath.getFileName().toString().toLowerCase(Locale.ROOT);
        ArticleDataReader reader;

        // Select the parsing strategy based on file extension.
        if (name.endsWith(".csv")) {
            reader = new CsvArticleDataReader();
        } else if (name.endsWith(".json")) {
            reader = new JsonArticleDataReader();
        } else {
            throw new IllegalArgumentException("Unsupported data file extension: " + dataPath);
        }

        List<Article> articles = reader.read(dataPath, logger);
        logger.info("Loaded " + articles.size() + " articles");

        return articles;
    }

    /**
     * Loads stop words from the configured stop word file.
     *
     * Each stop word is trimmed, normalized to lowercase, and inserted into
     * a HashSet for fast membership checks during indexing and topic analysis.
     *
     * @param stopWordsFile path to the stop words file
     * @return set of normalized stop words
     * @throws IOException if the file cannot be read
     */
    public static Set<String> loadStopWords(String stopWordsFile) throws IOException {
        Path stopWordsPath = resolveStopWordsPath(stopWordsFile);
        return Files.readAllLines(stopWordsPath)
                .stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Resolves the article data file path.
     *
     * Resolution order:
     * 1. direct path provided by the user
     * 2. path under the source directory
     * 3. fallback sample file for default CSV startup
     *
     * @param configuredPath configured article file path
     * @return resolved existing Path
     */
    private static Path resolveDataFilePath(String configuredPath) {
        String raw = (configuredPath == null || configuredPath.isBlank())
                ? "articles.csv"
                : configuredPath;

        Path direct = Paths.get(raw);
        if (Files.exists(direct)) {
            return direct;
        }

        Path underSource = Paths.get("src/main/java/edu/upenn/cit5940").resolve(raw);
        if (Files.exists(underSource)) {
            return underSource;
        }

        if ("articles.csv".equals(raw)) {
            Path fallback = Paths.get("src/main/java/edu/upenn/cit5940/articles_small.csv");
            if (Files.exists(fallback)) {
                return fallback;
            }
        }

        throw new IllegalArgumentException("Data file not found: " + raw);
    }

    /**
     * Resolves the stop words file path.
     *
     * Resolution order:
     * 1. direct path provided by the user
     * 2. path under the source directory
     *
     * @param configuredPath configured stop words file path
     * @return resolved existing Path
     */
    private static Path resolveStopWordsPath(String configuredPath) {
        String raw = (configuredPath == null || configuredPath.isBlank())
                ? "stop_words.txt"
                : configuredPath;

        Path direct = Paths.get(raw);
        if (Files.exists(direct)) {
            return direct;
        }

        Path underSource = Paths.get("src/main/java/edu/upenn/cit5940").resolve(raw);
        if (Files.exists(underSource)) {
            return underSource;
        }

        throw new IllegalArgumentException("Stop words file not found: " + raw);
    }
}